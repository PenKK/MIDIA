resource "aws_iam_role" "lambda_exec" {
  name = "midia-lambda-exec-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "lambda_logs" {
  role       = aws_iam_role.lambda_exec.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}


resource "aws_cloudwatch_log_group" "midia_lambda" {
  name              = "/aws/lambda/midia-backend"
  retention_in_days = 7
}


resource "aws_lambda_function" "midia_backend" {
  function_name = "midia-backend"

  package_type = "Image"

  image_uri = "${aws_ecr_repository.midia_backend.repository_url}:latest"

  role = aws_iam_role.lambda_exec.arn

  timeout = 10

  memory_size = 256

  architectures = ["arm64"]
}


resource "aws_lambda_function_url" "midia_backend" {
  function_name      = aws_lambda_function.midia_backend.function_name
  authorization_type = "NONE"
}


output "lambda_url" {
  value = aws_lambda_function_url.midia_backend.function_url
}