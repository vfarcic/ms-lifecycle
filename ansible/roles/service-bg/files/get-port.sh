#!/usr/bin/env bash

SERVICE_NAME=$1

COLOR=$2

echo `curl \
    localhost:8500/v1/catalog/service/$SERVICE_NAME-$COLOR \
    | jq '.[0].ServicePort'`
