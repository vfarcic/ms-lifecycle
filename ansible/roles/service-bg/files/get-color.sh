#!/usr/bin/env bash

SERVICE_NAME=$1

CURR_COLOR=`curl \
    http://localhost:8500/v1/kv/$SERVICE_NAME/color?raw`

if [ $CURR_COLOR == "green" ]; then
    NEXT_COLOR="blue"
else
    NEXT_COLOR="green"
fi

echo $NEXT_COLOR
