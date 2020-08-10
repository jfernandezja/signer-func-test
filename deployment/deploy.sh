#!/bin/bash

docker build -t oauth-service-func deployment/oauth-service
docker build -t signer-service-func deployment/signer-service

docker run -d -p 9080:8080 --rm --name oauth-service-func -t oauth-service-func 
docker run -d -p 9082:8080 --rm --name signer-service-func -t signer-service-func

sleep 5