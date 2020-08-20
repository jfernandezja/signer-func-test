#!/bin/bash

currentContainerId=$(cat /proc/self/cgroup | grep "docker" | sed s/\\//\\n/g | tail -1)
BRANCH=${GIT_BRANCH#"origin/"}

docker stop oauth-service-func-$BRANCH
docker stop signer-service-func-$BRANCH

docker network disconnect signer-network $currentContainerId
docker network rm signer-network