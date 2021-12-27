module "dynamodb_table" {
  source   = "terraform-aws-modules/dynamodb-table/aws"
  billing_mode = "PAY_PER_REQUEST"
  name     = "${var.project_name}-subscriptions"
  hash_key = "subscriptionId"

  attributes = [
    {
      name = "subscriptionId"
      type = "S"
    },
    {
      name = "recipientEmail"
      type = "S"
    },
    {
      name = "feedUrl",
      type = "S"
    }
  ]

  global_secondary_indexes = [
    {
      name               = "recipientIndex"
      hash_key           = "recipientEmail"
      range_key          = "feedUrl"
      projection_type    = "INCLUDE"
      non_key_attributes = ["description"]
    }
  ]  

  tags = {
    project_name = var.project_name
    environment = var.env
  }
}