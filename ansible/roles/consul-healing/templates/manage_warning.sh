#!/usr/bin/env bash

#RED='\033[0;31m'
#NC='\033[0;0m'

read -r JSON
#echo "Consul watch request:"
echo "$JSON"
#echo "$JSON" | jq -r '.[] | select(.CheckID | contains("service:")) | .ServiceName' | while read SERVICE_NAME
#do
#  echo ""
#  echo -e ">>> ${RED}Service $SERVICE_NAME is critical${NC}"
#  echo ""
#  echo "Triggering Jenkins job http://{{ ip }}:8080/job/$SERVICE_NAME/build"
#  curl -X POST http://{{ ip }}:8080/job/$SERVICE_NAME/build
#done
