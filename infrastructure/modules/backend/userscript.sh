#!/bin/bash

LOG=/etc/user-data.log

function install_dependencies() {

  echo "Installing aws CLI" >> $LOG
  yum install awscli jq -y
  echo "Installing docker with amazon-linux-extras" >> $LOG
  amazon-linux-extras install docker -y && \ 
  echo "Starting docker service" >> $LOG
  service docker start && \ 
  echo "Setting usermod for docker" >> $LOG
  usermod -a -G docker ec2-user
  echo "Ensure service starts on every reboot" >> $LOG
  chkconfig docker on

}

# DO ONCE, AND REBOOT
HAS_REBOOTED=/etc/rebooted
echo "Checking has rebooted" >> $LOG
if [ ! -f "$HAS_REBOOTED" ]; then

  echo "Installing dependencies" >> $LOG
  install_dependencies

  echo "Has not rebooted" >> $LOG
  touch $HAS_REBOOTED
  echo "Creating Has rebooted file" >> $LOG
  rm /var/lib/cloud/instances/*/sem/config_scripts_user >> $LOG
  echo "Removing has run user data semaphore" >> $LOG
  echo "Rebooting" >> $LOG
  reboot >> $LOG
fi

# login to ecr
echo "Preparing access to ECR" >> $LOG
aws_account_id=$(curl -s http://169.254.169.254/latest/dynamic/instance-identity/document | jq -r .accountId) >> $LOG
aws_region=$(curl -s http://169.254.169.254/latest/dynamic/instance-identity/document | jq .region -r) >> $LOG
aws ecr get-login-password --region $aws_region | docker login --username AWS --password-stdin $aws_account_id.dkr.ecr.$aws_region.amazonaws.com >> $LOG

# pull images to ec2 instance
echo "Pulling docker image" >> $LOG
docker pull $aws_account_id.dkr.ecr.$aws_region.amazonaws.com/rssmail:latest >> $LOG

