#!/usr/bin/env bash

set -- $(free -m | awk 'NR==2{print $2" "$3" "$7}')
total=$1
used=$2
cached=$3
used_percent=$(( ($used - $cached) * 100 / $total ))
printf "Memory Usage: %s/%sMB (%s%%)\n" $used $total $used_percent
if [ $used_percent -gt 95 ]; then
  exit 2
elif [ $used_percent -gt 85 ]; then
  exit 1
else
  exit 0
fi
