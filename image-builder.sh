#!/bin/sh
IMAGE=ekyc-service
VERSION=dev-1.0.0
REGISTRY=harbor-registry.lienvietpostbank.com.vn:5443/middleware

mvn clean install spring-boot:repackage -DskipTests
docker build -t ${IMAGE} -f dockerfile/Dockerfile-${IMAGE} .
docker tag ${IMAGE}:latest ${REGISTRY}/${IMAGE}:${VERSION}
docker push ${REGISTRY}/${IMAGE}:${VERSION}
