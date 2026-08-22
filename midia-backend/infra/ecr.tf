resource "aws_ecr_repository" "midia_backend" {
  name = "midia-backend"

  image_scanning_configuration {
    scan_on_push = false
  }
}

resource "aws_ecr_lifecycle_policy" "midia_backend" {
  repository = aws_ecr_repository.midia_backend.name

  policy = jsonencode({
    rules = [
      {
        rulePriority = 1
        description  = "Keep only the newest image"

        selection = {
          tagStatus   = "any"
          countType   = "imageCountMoreThan"
          countNumber = 1
        }

        action = {
          type = "expire"
        }
      }
    ]
  })
}

output "ecr_repository_url" {
  value = aws_ecr_repository.midia_backend.repository_url
}