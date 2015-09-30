#!/usr/bin/env bash

git clone https://github.com/vfarcic/books-ms.git

cd books-ms

docker build \
	-f Dockerfile.test \
	-t 10.100.198.200:5000/books-ms-tests \
	.

docker push 10.100.198.200:5000/books-ms-tests

docker build \
	-f Dockerfile \
	-t 10.100.198.200:5000/books-ms \
	.

docker push 10.100.198.200:5000/books-ms
