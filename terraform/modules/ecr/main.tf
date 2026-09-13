resource "aws_ecr_repository" "repos" {
  for_each = toset(var.repository_name)
  name = each.value
  image_tag_mutability = "IMMUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }
  tags = {
    Environment = var.environment
  }
}

resource "aws_ecr_lifecycle_policy" "cleanup" {
  for_each = aws_ecr_repository.repos
  repository = each.value.name

  policy = jsonencode({
    "rules" = [
        {
            "rulePriority" = 1,
            "description" = "Expire untagged images older than 7 days",
            "selection" : {
                "tagStatus" : "untagged",
                "countType" : "sinceImagePushed",
                "countUnit" : "days",
                "countNumber" : 7
            },
            "action" : {
                "type" : "expire"
            }
        }
    ]
  })
}