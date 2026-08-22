from sqlalchemy import Integer, String
from sqlalchemy.orm import Mapped, mapped_column, relationship

from src.auth.constants import EMAIL_MAX, PASSWORD_HASH_MAX, USERNAME_MAX
from src.database import Base
from src.project.models import DAWProject


class User(Base):
    __tablename__ = "users"

    id: Mapped[int] = mapped_column(Integer, primary_key=True)
    username: Mapped[str] = mapped_column(
        String(USERNAME_MAX), unique=True, index=True, nullable=False
    )
    email: Mapped[str] = mapped_column(
        String(EMAIL_MAX), primary_key=True, unique=True, index=True, nullable=False
    )
    password_hash: Mapped[str] = mapped_column(
        String(PASSWORD_HASH_MAX), nullable=False
    )

    projects: Mapped[list[DAWProject]] = relationship(back_populates="user_id")
