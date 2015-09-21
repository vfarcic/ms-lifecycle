#!/usr/bin/env bash

CURR_COLOR=`curl \
    http://localhost:8500/v1/kv/{{ service_name }}/color?raw`

if [ $CURR_COLOR == "green" ]; then
    NEXT_COLOR="blue"
else
    NEXT_COLOR="green"
fi

echo $NEXT_COLOR
