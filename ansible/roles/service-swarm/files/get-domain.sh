#!/usr/bin/env bash

SERVICE_NAME=$1

COLOR=$2

ADDRESS=`curl \
    localhost:8500/v1/catalog/service/books-ms \
    | jq -r '.[0].ServiceAddress + ":" + (.[0].ServicePort | tostring)'`

echo $ADDRESS
