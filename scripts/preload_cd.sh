#!/usr/bin/env bash

git clone https://github.com/vfarcic/books-ms.git
docker pull vfarcic/books-ms-tests
docker pull vfarcic/books-ms
docker pull mongo