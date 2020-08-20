#!/bin/bash

currentContainerId=$(cat /proc/self/cgroup | grep "docker" | sed s/\\//\\n/g | tail -1)
BRANCH=${GIT_BRANCH#"origin/"}

docker build -t oauth-service-func:$BRANCH --build-arg FROM_BRANCH=$BRANCH deployment/oauth-service
docker build -t signer-service-func:$BRANCH --build-arg FROM_BRANCH=$BRANCH deployment/signer-service

docker run -d --rm --name oauth-service-func-$BRANCH -t oauth-service-func:$BRANCH 
docker run -d --rm --name signer-service-func-$BRANCH -t signer-service-func:$BRANCH

docker network create signer-network
docker network connect signer-network $currentContainerId
docker network connect signer-network oauth-service-func-$BRANCH
docker network connect signer-network signer-service-func-$BRANCH