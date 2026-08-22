from datetime import datetime

from sqlalchemy import DateTime, ForeignKey, String
from sqlalchemy.dialects.postgresql import JSONB
from sqlalchemy.orm import Mapped, mapped_column

from src.database import Base
from src.project.constants import PROJECT_NAME_MAX
from src.util import utc_now


class DAWProject(Base):
    __tablename__ = "daw_projects"

    id: Mapped[int] = mapped_column(primary_key=True)
    user_id: Mapped[int] = mapped_column(
        ForeignKey("users.id"), nullable=False, index=True
    )
    data: Mapped[dict] = mapped_column(JSONB, nullable=False)
    project_name: Mapped[str] = mapped_column(String(PROJECT_NAME_MAX), nullable=False)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now
    )
    updated_at: Mapped[datetime] = mapped_column(default=utc_now, onupdate=utc_now)
