#!/usr/bin/env bash

git clone https://github.com/vfarcic/books-ms.git

set -e

cd books-ms

docker pull vfarcic/books-ms

docker tag vfarcic/books-ms 10.100.198.200:5000/books-ms

docker push 10.100.198.200:5000/books-ms

docker pull mongo

docker pull jenkins

apt-get install -y openjdk-7-jdk