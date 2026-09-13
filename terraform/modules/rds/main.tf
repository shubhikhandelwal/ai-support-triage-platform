resource "aws_security_group" "rdsSG" {
  name = "${var.environment}-rdsSG"
  vpc_id = var.vpc_id

  tags = {
    Environment = var.environment
  }

  lifecycle {
    create_before_destroy = true
  } //If the security group needs to be replaced, try to create the new one before destroying the old one. 
} 

resource "aws_vpc_security_group_ingress_rule" "rdsIngress" {
  for_each = toset(var.allowed_security_group_ids)
  security_group_id = aws_security_group.rdsSG.id
  from_port = 5432 //TCP port 5432 is generally used by Postgres
  to_port = 5432
  ip_protocol = "tcp" 
  referenced_security_group_id = each.value
}

resource "aws_vpc_security_group_egress_rule" "rdsEgress" {
  security_group_id = aws_security_group.rdsSG.id
  ip_protocol = "-1" //All protocols
  cidr_ipv4 = "0.0.0.0/0"
}

resource "aws_db_subnet_group" "main" {
  name = "${var.environment}-db-subnet-group"
  subnet_ids = var.private_subnet_ids
  tags = {
    Environment = var.environment
  }
}

resource "aws_db_instance" "main" {
   identifier     = "${var.environment}-triage-db"
  engine         = "postgres"
  engine_version = "16.9"
  instance_class = var.db_instance_class

  allocated_storage = 20
  storage_type      = "gp3" //General Purpose SSD (gp3) storage.

  username = var.db_username
  password = var.db_password

  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rdsSG.id]

  multi_az            = false //cost
  publicly_accessible = false //No public IP
  skip_final_snapshot = true //cost

  backup_retention_period = 1

  tags = {
    Environment = var.environment
  }
}