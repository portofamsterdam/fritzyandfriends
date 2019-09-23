#!/bin/bash

function usage {
	echo "Usage: ./build_images.sh <username> <password>"
	exit -1
}

if [ -z "$1"  ] || [ -z "$2" ] 
then
	usage
fi

echo "Building images as ${1}"

docker login -u $1 -p $2
cd build/fritzy
docker build -t fritzy2/fritzy:fritzy .
docker push fritzy2/fritzy:fritzy
cd ../batty
docker build -t fritzy2/fritzy:batty .
docker push fritzy2/fritzy:batty
cd ../netty
docker build -t fritzy2/fritzy:netty .
docker push fritzy2/fritzy:netty
cd ../sunny
docker build -t fritzy2/fritzy:sunny .
docker push fritzy2/fritzy:sunny
cd ../exxy
docker build -t fritzy2/fritzy:exxy .
docker push fritzy2/fritzy:exxy
