terraform {
  backend "s3" { 
    bucket       = "ai-support-triage-tfstate-triage"
    key          = "dev/terraform.tfstate"
    region       = "ap-south-1"
    encrypt      = true
    use_lockfile = true
  }
}