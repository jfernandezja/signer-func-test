#!/bin/bash

currentContainerId=$(cat /proc/self/cgroup | grep "docker" | sed s/\\//\\n/g | tail -1)

docker stop oauth-service-func
docker stop signer-service-func

docker network disconnect signer-network $currentContainerId
docker network disconnect signer-network oauth-service-func
docker network disconnect signer-network signer-service-func
docker network rm signer-network