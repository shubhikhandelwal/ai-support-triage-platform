resource "aws_sqs_queue" "dlq" {
  name = "${var.environment}-${var.queue_name}-dlq"
  message_retention_seconds = 120600 #14days

  tags = {
    Environment = var.environment
  }
}

resource "aws_sqs_queue" "main" {
  name = "${var.environment}-${var.queue_name}"
  visibility_timeout_seconds =  60
  message_retention_seconds = 245600 #4days
  
  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.dlq.arn
    maxReceiveCount     = var.max_receive_count
  })
  tags = {
    Environment = var.environment
  }
}