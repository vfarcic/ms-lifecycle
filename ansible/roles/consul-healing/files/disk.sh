#!/usr/bin/env bash

set -- $(df -h | awk '$NF=="/"{print $2" "$3" "$5}')
total=$1
used=$2
used_percent=${3::-1}
printf "Disk Usage: %s/%s (%s%%)\n" $used $total $used_percent
if [ $used_percent -gt 95 ]; then
  exit 2
elif [ $used_percent -gt 80 ]; then
  exit 1
else
  exit 0
fi
