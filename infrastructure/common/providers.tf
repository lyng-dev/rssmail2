terraform {
  backend "s3" {
    encrypt = true
    region = "us-east-1"
  }
  required_version = "1.1.1"
}

provider "aws" {
  region = "us-east-1"
}