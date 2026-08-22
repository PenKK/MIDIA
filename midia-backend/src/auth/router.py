from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from src.auth import models, schemas
from src.database import get_db

router = APIRouter(prefix="/auth", tags=["auth"])


@router.post(
    "/register",
    response_model=schemas.UserCreateResponse,
    status_code=status.HTTP_201_CREATED,
)
async def create_user(user: schemas.UserCreate, db: Session = Depends(get_db)):
    result = await db.execute(
        select(models.User).where(models.User.email == user.email)
    )
