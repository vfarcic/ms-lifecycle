#!/usr/bin/env bash

git clone https://github.com/vfarcic/books-ms.git
docker pull vfarcic/books-ms-tests
docker pull vfarcic/books-ms
docker pull mongo
docker pull gilderlabs/registrator
docker tag gilderlabs/registrator 10.100.198.200:5000/registrator
docker push 10.100.198.200:5000/registrator
docker pull nginx
docker tag nginx 10.100.198.200:5000/nginx
docker push 10.100.198.200:5000/nginx
docker pull mongo
docker tag mongo 10.100.198.200:5000/mongo
docker push 10.100.198.200:5000/mongo
