#!/bin/bash

docker build -t oauh-service-func --name oauth-service-func src/test/resources/oauth-service
docker build -t signer-service-func --name signer-service-func src/test/resources/signer-service

docker run -p 9080:8080 -t oauth-service-func
docker run -p 9082:8080 -t signer-service-func