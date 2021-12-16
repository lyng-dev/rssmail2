#!/bin/bash

if [ "$#" -lt 2 ]; then
    echo "Usage: $0 <env> <command>"
    echo " - Description: Shell script that wraps terraform and adds environment"
    echo " - Example: $0 test apply"
    exit 1
fi

ENV=$1
shift 1 
CMD=$@

(
  cd infrastructure/terraform/$ENV
  source .env
  terraform init
  AWS_PROFILE=rssmail terraform $CMD
)