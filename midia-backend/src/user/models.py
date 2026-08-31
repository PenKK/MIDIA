from __future__ import annotations

from datetime import datetime
from typing import TYPE_CHECKING

from sqlalchemy import Integer, String, DateTime
from sqlalchemy.orm import Mapped, mapped_column, relationship

from src.user.constants import EMAIL_MAX, PASSWORD_HASH_MAX, USERNAME_MAX
from src.database import Base
from src.util import utc_now


if TYPE_CHECKING:
    from src.project.models import DAWProject


class User(Base):
    __tablename__ = "users"

    id: Mapped[int] = mapped_column(Integer, primary_key=True)
    username: Mapped[str] = mapped_column(
        String(USERNAME_MAX), unique=True, index=True, nullable=False
    )
    email: Mapped[str] = mapped_column(
        String(EMAIL_MAX), unique=True, index=True, nullable=False
    )
    password_hash: Mapped[str] = mapped_column(
        String(PASSWORD_HASH_MAX), nullable=False
    )
    date_created: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), nullable=False, default=utc_now
    )

    projects: Mapped[list[DAWProject]] = relationship(back_populates="user")