from pydantic import SecretStr
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")

    # JWT
    secret_key: SecretStr
    algorithm: str = "HS256"
    access_token_expire_minutes: int = 30

    # DB
    db_name: str
    db_user: str
    db_password: SecretStr
    db_host: str
    db_port: str = 5432


settings = Settings()  # type: ignore[call-arg] # Loaded from .env file
