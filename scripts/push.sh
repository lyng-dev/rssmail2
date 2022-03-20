#!/bin/bash

TAG=$1

(
  #load settings
  source .env

  #upload
  aws ecr get-login-password --region $AWS_REGION --profile $AWS_PROFILE | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com
  docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/rssmail:$TAG

  #remove current tag
  aws ecr batch-delete-image --repository-name rssmail --profile $AWS_PROFILE --image-ids imageTag=latest --region $AWS_REGION || true #allow failure, because there might not be a latest

  #retag with latest
  MANIFEST=$(aws ecr batch-get-image --profile $AWS_PROFILE --repository-name rssmail --image-ids imageTag=$TAG --region $AWS_REGION --output json | jq --raw-output '.images[0].imageManifest')
  aws ecr put-image --profile $AWS_PROFILE --repository-name rssmail --image-tag latest --region $AWS_REGION --image-manifest "$MANIFEST"
)
