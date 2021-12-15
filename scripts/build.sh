#!/bin/bash

set -x
set -e

docker build . -f docker/Dockerfile -t rssmail