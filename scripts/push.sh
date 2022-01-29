#!/bin/bash

TAG=$1

(
  #load settings
  source .env

  #upload
  aws ecr get-login-password --region us-east-1 --profile rssmail | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com
  docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/rssmail:$TAG

  #remove current tag
  aws ecr batch-delete-image --repository-name rssmail --profile rssmail --image-ids imageTag=latest || true #allow failure, because there might not be a latest

  #retag with latest
  MANIFEST=$(aws ecr batch-get-image --profile rssmail --repository-name rssmail --image-ids imageTag=$TAG --output json | jq --raw-output '.images[0].imageManifest')
  aws ecr put-image --profile rssmail --repository-name rssmail --image-tag latest --image-manifest "$MANIFEST"
)
