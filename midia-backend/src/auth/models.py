from sqlalchemy.orm import Mapped, mapped_column
from sqlalchemy import String, Integer
from src.database import Base
from src.auth.constants import USERNAME_MAX, EMAIL_MAX, PASSWORD_HASH_MAX


class User(Base):
    __tablename__ = "users"
    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True, unique=True)
    username: Mapped[str] = mapped_column(String(USERNAME_MAX), unique=True, index=True, nullable=False)
    email: Mapped[str] = mapped_column(String(EMAIL_MAX), primary_key=True, unique=True, index=True, nullable=False)
    password_hash: Mapped[str] = mapped_column(String(PASSWORD_HASH_MAX), nullable=False)
