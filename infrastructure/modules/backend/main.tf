# resource "aws_instance" "rssmail-backend" {
#   ami           = "ami-0a8b4cd432b1c3063" # us-east-1
#   instance_type = "t3.nano"

#   user_data = file("${path.module}/userscript.sh")

#   credit_specification {
#     cpu_credits = "unlimited"
#   }
# }