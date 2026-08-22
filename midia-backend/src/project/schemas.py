from pydantic import BaseModel, ConfigDict
from sqlalchemy import DateTime


class ProjectBase(BaseModel):
    pass


class ProjectResponse(ProjectBase):
    model_config = ConfigDict(from_attributes=True)

    id: int
    user_id: int
    project_name: str
    created_at: DateTime
    updated_at: DateTime
