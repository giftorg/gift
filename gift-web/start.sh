# /bin/bash

if [[ "$(docker images -q gift_web:latest 2> /dev/null)" == "" ]]; then
    docker build -t gift_web .
fi

docker rm -f gift_web

docker run --name gift_web -id -e API_HOST=$1 -p 80:80 gift_web:latest
