terraform {
  backend "s3" {
    bucket = "rssmail-terraform-tfstate-dev"
    dynamodb_table = "terraform-lock"
    key = "rssmail/tfstate"
    encrypt = true
    region = "us-east-1"
    profile = "rssmail"
  }
}