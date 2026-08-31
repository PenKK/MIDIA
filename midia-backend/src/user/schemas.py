from datetime import datetime

from pydantic import BaseModel, ConfigDict, EmailStr, Field

from src.user.constants import PASSWORD_MIN, USERNAME_MAX, USERNAME_MIN


class UserBase(BaseModel):
    username: str = Field(min_length=USERNAME_MIN, max_length=USERNAME_MAX)
    email: EmailStr


class UserCreate(UserBase):
    password: str = Field(min_length=PASSWORD_MIN)


class UserCreateResponse(UserBase):
    model_config = ConfigDict(from_attributes=True)
    id: int
    date_created: datetime


class Token(BaseModel):
    access_token: str
    token_type: str
