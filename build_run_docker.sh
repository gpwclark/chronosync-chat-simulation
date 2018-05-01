#!/bin/bash

docker build -t nfd .
for var in "$@"
do
    echo "stop,rm, and run nfd container $var"
    docker stop $var
    docker rm $var
    docker run -d --net host --name $var -v $(pwd)/nfd_conf:/nfd/etc nfd -c /nfd/etc/nfd.conf
done

