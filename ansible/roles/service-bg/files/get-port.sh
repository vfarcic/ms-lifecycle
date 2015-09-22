#!/usr/bin/env bash

COLOR=$1

NEXT_PORT=`curl \
    localhost:8500/v1/catalog/service/books-ms-$COLOR \
    | jq '.[0].ServicePort'`

echo $NEXT_PORT
