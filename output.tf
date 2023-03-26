output "instance_id" {
  description = "ID of the EC2 instance"
  value       = aws_instance.kajal-gurukul.id
}
output "instance_public_ip" {
  description = "Public IP address of the EC2 instance"
  value       = aws_instance.kajal-gurukul.public_ip
}

output "private_key" {
  value     = tls_private_key.private-key.private_key_pem
  sensitive = true
}

output "instance_dns" {
  description = "Public dns"
  value       = aws_instance.kajal-gurukul.public_dns
}
