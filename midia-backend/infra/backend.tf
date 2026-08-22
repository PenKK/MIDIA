terraform {
  backend "s3" {
    bucket = "midia-terraform-state"
    key    = "midia/terraform.tfstate"
    region = "us-east-1"
  }
}