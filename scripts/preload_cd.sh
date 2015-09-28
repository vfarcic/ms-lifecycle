#!/usr/bin/env bash

git clone https://github.com/vfarcic/books-ms.git
docker pull vfarcic/books-ms-tests
docker tag vfarcic/books-ms-tests 10.100.198.200:5000/books-ms-tests
docker push 10.100.198.200:5000/books-ms-tests
docker pull vfarcic/books-ms
docker tag vfarcic/books-ms 10.100.198.200:5000/books-ms
docker push 10.100.198.200:5000/books-ms
