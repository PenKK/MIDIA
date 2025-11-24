from django.contrib.auth import authenticate
from rest_framework import status
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework_simplejwt.tokens import RefreshToken

from users.models import User
from users.serializers import UserRegistrationSerializer

@api_view(['POST'])
def register_user(request):
    serializer = UserRegistrationSerializer(data=request.data)
    if serializer.is_valid():
        serializer.save()
        return Response({
            "message": "User created successfully",
            "user": serializer.data},
            status=status.HTTP_201_CREATED)
    return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

@api_view(['POST'])
def login_user(request):
    email = request.data.get('email')

    try:
        user_obj = User.objects.get(email=email)
    except User.DoesNotExist:
        return Response({"message": "Invalid credentials"},
                        status=status.HTTP_401_UNAUTHORIZED)

    user = authenticate(request,
                        username=user_obj.username,
                        password=request.data.get('password'))

    if not user:
        return Response({"message": "Invalid credentials"},
                        status=status.HTTP_401_UNAUTHORIZED)

    refresh = RefreshToken.for_user(user)
    return Response({"message": "Login successful",
                     "username": user.get_username(),
                     "access": str(refresh.access_token),
                     "refresh": str(refresh)})