#!/usr/bin/env bash

COLOR=$1

ADDRESS=`curl \
    localhost:8500/v1/catalog/service/books-ms-$COLOR \
    | jq '.[0].ServiceAddress'`

PORT=`curl \
    localhost:8500/v1/catalog/service/books-ms-$COLOR \
    | jq '.[0].ServicePort'`

echo ${ADDRESS//\"}:$PORT
