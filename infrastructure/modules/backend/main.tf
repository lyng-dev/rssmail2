resource "aws_instance" "rssmail-backend" {
  
  #ami           = "ami-0a8b4cd432b1c3063" # us-east-1
  ami = "ami-0a8b4cd432b1c3063" #amazon linux 2 us-east-1
  instance_type = "t3.nano"

  iam_instance_profile = aws_iam_instance_profile.backend-profile.name

  key_name = "rssmail-backend"

  user_data = file("${path.module}/userscript.sh")

  vpc_security_group_ids = [aws_security_group.allow-ssh.id, aws_security_group.allow-http.id]

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
      {
        Action = [
          "dynamodb:*",
        ]
        Effect   = "Allow"
        Resource = "*"
      },
    ]
  })
}

# security group
resource "aws_security_group" "allow-ssh" {
  name        = "allow_ssh"
  description = "Allow inbound traffic"
  vpc_id      = var.vpc_id

  ingress {
    description      = "Inbound SSH"
    from_port        = 22
    to_port          = 22
    protocol         = "tcp"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }
  egress {
    description      = "allow all"
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }
}

resource "aws_security_group" "allow-http" {
  name        = "allow_http"
  description = "Allow inbound traffic"
  vpc_id      = var.vpc_id

  ingress {
    description      = "Inbound SSH"
    from_port        = 8080
    to_port          = 8080
    protocol         = "tcp"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }
  egress {
    description      = "allow all"
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }
}
