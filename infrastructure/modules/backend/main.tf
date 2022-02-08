resource "aws_instance" "rssmail-backend" {
  
  #ami           = "ami-0a8b4cd432b1c3063" # us-east-1
  ami = "ami-01b996646377b6619" #ubuntu
  instance_type = "t3.nano"

  key_name = "rssmail-backend"

  user_data = file("${path.module}/userscript.sh")

  credit_specification {
    cpu_credits = "unlimited"
  }
}

resource "aws_iam_role" "backend" {
  name = "rssmail-backend"

  # Terraform's "jsonencode" function converts a
  # Terraform expression result to valid JSON syntax.
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Sid    = ""
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      },
    ]
  })
}

resource "aws_iam_instance_profile" "backend-profile" {
  name = "backend-profile"
  role = aws_iam_role.backend.name
}

resource "aws_iam_role_policy" "backend-role-policy" {
  name = "backend-role-policy"
  role = aws_iam_role.backend.id

  # Terraform's "jsonencode" function converts a
  # Terraform expression result to valid JSON syntax.
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "ecr:*",
        ]
        Effect   = "Allow"
        Resource = "*"
      },
    ]
  })
}