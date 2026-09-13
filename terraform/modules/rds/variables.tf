variable "environment" {
  description = "Environment"
  type = string
}

variable "vpc_id" {
  description = "The id of the VPC"
  type = string
}

variable "private_subnet_ids" {
  description = "The ids of the subnet to keep the RDS db in"
  type = list(string)
}

variable "db_instance_class" {
  type    = string
  default = "db.t3.micro"
}

variable "db_username" {
  type      = string
  sensitive = true
}

variable "db_password" {
  type      = string
  sensitive = true
}

variable "allowed_security_group_ids" {
  description = "Security groups allowed to connect to RDS"
  type        = list(string)
  default     = []
}