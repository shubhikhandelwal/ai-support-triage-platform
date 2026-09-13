variable "environment" {
  description = "Environment name"
  type = string
}

variable "repository_name" {
  description = "List of ECR repo names to create"
  type = list(string)
}