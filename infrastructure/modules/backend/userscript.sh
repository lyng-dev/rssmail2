#!/bin/bash

LOG=/etc/user-data.log

function install_dependencies() {

  echo "Installing docker with amazon-linux-extras" >> $LOG
  sudo amazon-linux-extras install docker -y && \ 
  echo "Starting docker service" >> $LOG
  sudo service docker start && \ 
  echo "Setting usermod for docker" >> $LOG
  sudo usermod -a -G docker ec2-user
  echo "Ensure service starts on every reboot" >> $LOG
  sudo chkconfig docker on

}

# Do single reboot
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
  echo "Rebooting">> $LOG
  reboot >> $LOG
fi

echo "After reboot sequence" >> $LOG