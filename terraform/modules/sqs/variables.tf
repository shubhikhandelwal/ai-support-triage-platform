variable "environment" {
  type = string
}

variable "queue_name" {
  type    = string
  default = "ticket-created-queue"
}

variable "max_receive_count" {
  description = "Number of processing failures before a message goes to the DLQ"
  type        = number
  default     = 3
}