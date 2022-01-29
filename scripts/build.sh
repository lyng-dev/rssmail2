#!/bin/bash

if (( $# < 1 ))
then
    printf "%b" "Error. Not enough arguments.\n" >&2
    printf "%b" "usage: $0 <finalName>\n" >&2
    exit 1
fi

FINAL_NAME=$1

DOCKER_BUILDKIT=1 docker build . -f docker/Dockerfile -t rssmail --build-arg finalName=$FINAL_NAME