terraform {
  backend "s3" {
    bucket  = "kajal-gurukul-bucket"
    key     = "s3/terraform.tfstate"
    region  = "us-east-1"
    encrypt = true
  }
}