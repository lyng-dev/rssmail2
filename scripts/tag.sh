#!/bin/bash

if (( $# < 2 )); then
    printf "%b" "Error. Not enough arguments.\n" >&2
    printf "%b" "usage: $0 <currentTag> <finalName>\n" >&2
    printf "%b" "example: $0 latest 0.0.1-beta" >&2
    exit 1
fi
(
    source .env
    docker tag rssmail:$1 $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/rssmail:$2
)
