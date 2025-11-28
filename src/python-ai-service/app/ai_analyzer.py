import json
import os
import time
from typing import Dict, List, Optional
from openai import OpenAI, APIStatusError, AuthenticationError, PermissionDeniedError, RateLimitError
from app.config import settings

# --- 清除代理 ---
for proxy_var in ['http_proxy', 'https_proxy', 'all_proxy', 'HTTP_PROXY', 'HTTPS_PROXY', 'ALL_PROXY']:
    os.environ.pop(proxy_var, None)

try:
    client = OpenAI(
        api_key=settings.api_key,
        base_url=settings.api_base,
    )
except Exception as e:
    print(f"OpenAI 客户端初始化失败: {e}")

class AIAnalyzer:
    @staticmethod
    def _call_ai_service(prompt: str, system_message: str) -> str:
        """封装 AI 服务的核心调用逻辑，处理 1.x 版本的异常"""
        try:
            response = client.chat.completions.create(
                model=settings.model_name,
                messages=[{"role": "system", "content": system_message},
                          {"role": "user", "content": prompt}],
                max_tokens=settings.max_tokens,
                temperature=settings.temperature,
            )
            return response.choices[0].message.content.strip()

        except AuthenticationError as e:
            print(f"OpenAI 认证错误 (401): 请检查 API_KEY 或 API_BASE 是否正确。详细: {e}")
            raise Exception("AI服务调用失败: 认证失败")
        except RateLimitError as e:
            print(f"OpenAI 速率限制错误 (429): 请等待或增加配额。详细: {e}")
            raise Exception("AI服务调用失败: 速率限制")
        except (APIStatusError, PermissionDeniedError) as e:
            # 捕获其他 4xx/5xx 状态码错误
            print(f"OpenAI API 状态错误 ({e.status_code}): {e.message}")
            raise Exception(f"AI服务调用失败 (状态码 {e.status_code}): {e.message}")
        except Exception as e:
            # 捕获其他所有错误 (例如网络错误)
            print(f"AI服务连接或未知错误: {e}")
            raise Exception(f"AI服务连接或未知错误: {e.__class__.__name__}")

    @staticmethod
    def analyze_student_submission(homework_title: str,
                                   homework_description: str,
                                   student_content: str,
                                   teacher_feedback: Optional[str] = None,
                                   grade: Optional[float] = None) -> Dict:
        """分析学生作业并生成学情报告"""

        prompt = f"""
        请分析以下学生作业并生成详细的学情报告：

        作业标题：{homework_title}
        作业要求：{homework_description}
        学生答案：{student_content}

        {f"教师评分：{grade}" if grade is not None else ""}
        {f"教师反馈：{teacher_feedback}" if teacher_feedback else ""}

        请从以下几个方面生成学情报告：
        1. 知识掌握程度分析 (字段名: knowledge_mastery)
        2. 优点与不足 (字段名: strengths_and_weaknesses)
        3. 改进建议 (字段名: improvement_suggestions)
        4. 学习资源推荐 (字段名: resource_recommendations)
        5. 预计学习时间 (字段名: estimated_time)

        请以JSON格式返回，包含上述五个字段。
        """

        system_message = "你是一个经验丰富的教育专家，擅长分析学生作业情况并提供专业的学情分析报告。请严格以JSON对象格式返回。"

        response_content = AIAnalyzer._call_ai_service(prompt, system_message)

        # JSON 解析
        try:
            analysis_report = json.loads(response_content)
            return analysis_report
        except json.JSONDecodeError as e:
            print(f"JSON解析失败: {e}. 原始返回内容: {response_content[:500]}...")
            raise Exception(f"AI返回内容无法解析为JSON: {e.__class__.__name__}")

    @staticmethod
    def generate_teaching_suggestions(homework_title: str,
                                      homework_description: str,
                                      student_submissions: List[Dict]) -> List[Dict]:
        """生成教师教学建议"""

        submission_summaries = []
        for i, sub in enumerate(student_submissions[:5]):  # 取前5个作为样本
            submission_summaries.append(f"""
            学生{i + 1}:
            - 答案内容: {sub['content'][:200]}...
            - 评分: {sub.get('grade', '未评分')}
            - 状态: {sub.get('status', '未知')}
            """)

        summary_text = "\n".join(submission_summaries)

        prompt = f"""
        请基于以下作业信息和学生提交情况，生成教师教学建议：

        作业标题：{homework_title}
        作业要求：{homework_description}
        学生提交摘要：
        {summary_text}

        请生成以下几方面的教学建议：
        1. 整体掌握情况分析 (title: 整体掌握情况分析, content: ...)
        2. 常见错误分析 (title: 常见错误分析, content: ...)
        3. 针对性教学策略 (title: 针对性教学策略, content: ...)
        4. 课堂活动建议 (title: 课堂活动建议, content: ...)
        5. 后续作业设计思路 (title: 后续作业设计思路, content: ...)

        请严格以JSON数组格式返回，每个建议是一个包含title和content字段的对象。
        """

        system_message = "你是一个经验丰富的教育专家，擅长分析学生作业情况并提供专业的教学建议，请严格以JSON数组格式返回。"

        response_content = AIAnalyzer._call_ai_service(prompt, system_message)

        # JSON 解析
        try:
            suggestions = json.loads(response_content)
            # 确保解析结果是 List
            if isinstance(suggestions, List):
                return suggestions
            else:
                raise TypeError("AI返回内容不是预期的JSON数组结构。")
        except (json.JSONDecodeError, TypeError) as e:
            print(f"JSON解析失败: {e}. 原始返回内容: {response_content[:500]}...")
            raise Exception(f"AI返回内容无法解析为JSON数组: {e.__class__.__name__}")