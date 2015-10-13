#!/usr/bin/env bash

set -e

docker pull mongo

docker pull nginx

docker pull 10.100.198.200:5000/books-ms

docker pull gliderlabs/registrator