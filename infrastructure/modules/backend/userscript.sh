#!/bin/bash

# prepare yum
sudo yum update

# dependency for json
sudo yum install jq 

# dependency for docker
sudo yum install docker -y
wget https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) 
sudo mv docker-compose-$(uname -s)-$(uname -m) /usr/local/bin/docker-compose
sudo chmod -v +x /usr/local/bin/docker-compose
sudo systemctl enable docker.service
sudo systemctl start docker.service

# ensure that docker has started
until docker version > /dev/null 2>&1
do 
  sleep 1
done

# login to ecr
aws_account_id = $(curl -s http://169.254.169.254/latest/dynamic/instance-identity/document | jq -r .accountId)
aws_region = $(curl -s http://169.254.169.254/latest/dynamic/instance-identity/document | jq .region -r)
aws ecr get-login-password --region $aws_region | docker login --username AWS --password-stdin $aws_account_id.dkr.ecr.$aws_region.amazonaws.com

# pull images to ec2 instance
docker pull $aws_account_id.dkr.ecr.$aws_region.amazonaws.com/rssmail:latest
