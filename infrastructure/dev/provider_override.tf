terraform {
  backend "s3" {
    bucket = "rssmail-tfstate-dev"
    dynamodb_table = "rssmail-tfstate-lock"
    key = "rssmail/tfstate"
    encrypt = true
    region = "us-east-1"
  }
}