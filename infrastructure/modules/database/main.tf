module "dynamodb_table" {
  source   = "terraform-aws-modules/dynamodb-table/aws"
  billing_mode = "PAY_PER_REQUEST"
  name     = "${var.project_name}-subscriptions"
  hash_key = "id"

  attributes = [
    {
      name = "id"
      type = "N"
    },
    {
      name = "email"
      type = "S"
    },
    {
      name = "feedUrl",
      type = "S"
    }
  ]

  tags = {
    project_name = var.project_name
    environment = var.env
  }
}