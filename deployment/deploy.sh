#!/bin/bash

currentContainerId=$(cat /proc/self/cgroup | grep "docker" | sed s/\\//\\n/g | tail -1)

docker build -t oauth-service-func --build-arg FROM_BRANCH=${GIT_BRANCH#"origin/"} deployment/oauth-service
docker build -t signer-service-func deployment/signer-service

docker run -d --rm --name oauth-service-func -t oauth-service-func 
docker run -d --rm --name signer-service-func -t signer-service-func

docker network create signer-network
docker network connect signer-network $currentContainerId
docker network connect signer-network oauth-service-func
docker network connect signer-network signer-service-func