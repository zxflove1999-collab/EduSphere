from pydantic_settings import BaseSettings
from dotenv import load_dotenv

load_dotenv()

class Settings(BaseSettings):
    # API 认证配置
    api_key: str = "YOUR_REAL_API_KEY_HERE"
    api_base: str = "https://ark.cn-beijing.volces.com/api/v3"
    model_name: str = "doubao-seed-1-6-251015"

    # 应用配置
    host: str = "0.0.0.0"
    port: int = 8000

    # AI 调用参数
    max_tokens: int = 4096
    temperature: float = 0.5

    class Config:
        env_file = ".env"

settings = Settings()