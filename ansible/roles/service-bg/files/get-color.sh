#!/usr/bin/env bash

SERVICE_NAME=$1

CURR_COLOR=`curl \
    http://localhost:8500/v1/kv/$SERVICE_NAME/color?raw`

if [ $CURR_COLOR == "blue" ]; then
    NEXT_COLOR="green"
else
    NEXT_COLOR="blue"
fi

echo $NEXT_COLOR
