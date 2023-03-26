terraform {
  backend "s3" {
    bucket  = "kajal-gurukul-bucket"
    key     = "s3/terraform.tfstate"
    region  = "us-east-1"
    encrypt = true
  }
}

provider "aws" {
  region = "us-east-1"
}

resource "tls_private_key" "private-key" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

resource "aws_key_pair" "generated_key" {
  key_name   = "gurukul-key"
  public_key = tls_private_key.private-key.public_key_openssh

}


data "aws_vpc" "kajal-vpc" {
  id = "vpc-019c09a1a0c5b4f6b"
}

resource "aws_subnet" "kajal-subnet" {
  vpc_id     = data.aws_vpc.kajal-vpc.id
  cidr_block = "10.0.0.80/28"
  tags = {
    Name = "kajal"
  }
}


resource "aws_instance" "kajal-gurukul" {
  ami                         = "ami-00c39f71452c08778"
  instance_type               = "t2.micro"
  associate_public_ip_address = true
  key_name                    = "gurukul-key"
  vpc_security_group_ids      = [aws_security_group.kajal-sg.id]
  subnet_id                   = aws_subnet.kajal-subnet.id
  tags = {
    Name = "kajal-gurukul"
  }

}

resource "aws_security_group" "kajal-sg" {
  name   = "kajal-sg"
  vpc_id = data.aws_vpc.kajal-vpc.id
  egress = [
    {
      cidr_blocks      = ["0.0.0.0/0", ]
      description      = ""
      from_port        = 0
      ipv6_cidr_blocks = []
      prefix_list_ids  = []
      protocol         = "-1"
      security_groups  = []
      self             = false
      to_port          = 0
    }
  ]
  ingress = [
    {
      cidr_blocks      = ["0.0.0.0/0", ]
      description      = ""
      from_port        = 22
      ipv6_cidr_blocks = []
      prefix_list_ids  = []
      protocol         = "tcp"
      security_groups  = []
      self             = false
      to_port          = 22
    }
  ]
}



