module "dynamodb_table" {
  source   = "terraform-aws-modules/dynamodb-table/aws"
  billing_mode = "PAY_PER_REQUEST"
  name     = "${var.project_name}-subscriptions"
  hash_key = "id"

  attributes = [
    {
      name = "id"
      type = "N"
    }
  ]

  tags = {
    project_name = var.project_name
    environment = var.env
  }
}