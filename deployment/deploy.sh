#!/bin/bash

docker build -t oauh-service-func deployment/oauth-service
docker build -t signer-service-func deployment/signer-service

docker run -p 9080:8080 --name oauth-service-func -t oauth-service-func 
docker run -p 9082:8080 --name signer-service-func -t signer-service-func