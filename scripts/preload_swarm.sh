#!/usr/bin/env bash

docker pull swarm
docker tag swarm 10.100.198.200:5000/swarm
docker push 10.100.198.200:5000/swarm
