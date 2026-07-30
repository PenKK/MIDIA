from pydantic import BaseModel, ConfigDict, Field, EmailStr

from src.auth.constants import USERNAME_MIN, USERNAME_MAX, EMAIL_MAX, PASSWORD_MIN


class UserBase(BaseModel):
    username: str = Field(min_length=USERNAME_MIN, max_length=USERNAME_MAX)
    email: EmailStr


class UserCreate(UserBase):
    password: str = Field(min_length=PASSWORD_MIN)


class UserResponse(UserBase):
    model_config = ConfigDict(from_attributes=True)
    id: int

class Token(BaseModel):
    access_token: str
    token_type: str