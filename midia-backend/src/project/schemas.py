from datetime import datetime

from pydantic import BaseModel, ConfigDict, Field

from project.constants import PROJECT_NAME_MAX


class ProjectBase(BaseModel):
    project_name: str = Field(min_length=1, max_length=PROJECT_NAME_MAX)
    data: dict


class ProjectCreate(ProjectBase):
    pass


class ProjectUpdate(BaseModel):
    project_name: str | None = Field(
        default=None, min_length=1, max_length=PROJECT_NAME_MAX
    )
    data: dict | None = None


class ProjectResponse(ProjectBase):
    model_config = ConfigDict(from_attributes=True)

    id: int
    user_id: int
    created_at: datetime
    updated_at: datetime