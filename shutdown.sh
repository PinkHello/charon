#!/bin/bash

echo "===> shutdown docker-compose"
docker-compose -f docker-compose.yml down

echo "===> remove kafka-connect"
docker rmi kafka-connect:1.0.0
