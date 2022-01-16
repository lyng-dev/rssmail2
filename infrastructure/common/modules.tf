module "database-subscriptions" {
  source = "../modules/database"
  env = var.env
  project_name = var.project_name
}

module "email-service" {
  source = "../modules/email"
  env = var.env
  project_name = var.project_name
}