from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from typing import List, Optional, Dict
import json
from app.ai_analyzer import AIAnalyzer
from app.config import settings
from starlette.responses import JSONResponse  # 导入JSONResponse用于自定义响应

# 核心应用实例
app = FastAPI(title="EduSphere AI分析服务")


# ===============================================
# Pydantic 请求模型
# ===============================================

class StudentAnalysisRequest(BaseModel):
    # 字段可选，附上默认值。若需必要则去点Optional
    homework_title: Optional[str] = Field(default="未命名作业", description="作业标题")
    homework_description: Optional[str] = Field(default="暂无描述", description="作业描述")
    student_content: Optional[str] = Field(default="", description="学生提交内容")
    teacher_feedback: Optional[str] = None
    grade: Optional[float] = None


class TeachingSuggestionsRequest(BaseModel):
    homework_title: str
    homework_description: str
    student_submissions: List[Dict]


# ===============================================
# 路由定义
# ===============================================

@app.post("/api/analyze/student")
async def analyze_student_submission(request: StudentAnalysisRequest):
    """分析学生作业并生成学情报告"""
    try:
        # 输入验证
        if not request.student_content or request.student_content.strip() == "":
            raise ValueError("学生提交内容不能为空")

        result = AIAnalyzer.analyze_student_submission(
            request.homework_title or "未命名作业",
            request.homework_description or "暂无描述",
            request.student_content,
            request.teacher_feedback,
            request.grade
        )
        # 返回结构
        return {"success": True, "data": result}
    except ValueError as e:
        # 400 错误：请求数据问题
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        # 500 错误：AI调用问题
        print(f"Error during student analysis: {e}")
        raise HTTPException(status_code=500, detail=f"分析失败: {e.__class__.__name__}")


@app.post("/api/analyze/teaching")
async def generate_teaching_suggestions(request: TeachingSuggestionsRequest):
    """生成教师教学建议"""
    try:
        result = AIAnalyzer.generate_teaching_suggestions(
            request.homework_title,
            request.homework_description,
            request.student_submissions
        )
        # 返回结构
        return {"success": True, "data": result}
    except Exception as e:
        print(f"Error during teaching suggestions generation: {e}")  # 打印详细日志
        raise HTTPException(status_code=500, detail=f"生成建议失败: {e.__class__.__name__}")


@app.get("/")
async def root():
    return {"message": "EduSphere AI分析服务运行中", "version": "1.0"}


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host=settings.host, port=settings.port)