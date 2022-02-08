#!/bin/bash

if (( $# < 1 )); then
    printf "%b" "Error. Not enough arguments.\n" >&2
    printf "%b" "usage: $0 <tag>\n" >&2
    exit 1
fi

./scripts/build.sh rssmail
./scripts/tag.sh latest $1
./scripts/push.sh $1