module "networking" {
  source = "../../modules/networking"

  environment = var.environment
  vpc_cidr = var.vpc_cidr
  azs = var.azs
  public_subnet_cidrs = var.public_subnet_cidrs
  private_subnet_cidrs = var.private_subnet_cidrs
}

module "ecr" {
  source = "../../modules/ecr"
  environment = var.environment
  repository_name = [ "user-service", "ticket-service", "triage-service" ]
}

module "rds" {
  source = "../../modules/rds"
  environment = var.environment
  vpc_id              = module.networking.vpc_id
  private_subnet_ids  = module.networking.private_subnet_ids
  db_username         = var.db_username
  db_password         = var.db_password
  allowed_security_group_ids = [module.eks.node_security_group_id]
}

module "sqs" {
  source = "../../modules/sqs"

  environment = var.environment
  queue_name  = "ticket-created-queue"
}

module "eks" {
  source = "../../modules/eks"
  environment = var.environment
  cluster_name = "${var.environment}-ai-triage-cluster"
  vpc_id = module.networking.vpc_id
  private_subnet_ids = module.networking.private_subnet_ids
}