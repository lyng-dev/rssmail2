resource "aws_ecr_repository" "ecr" {
  name                 = "rssmail-backend"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }
}

output "ecr_repository_arn" {
    value = aws_ecr_repository.ecr.arn
}

output "ecr_repository_url" {
    value = aws_ecr_repository.ecr.repository_url
}