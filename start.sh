#!/bin/bash

echo "===> build app"
gradle clean jar

echo "===> build kafka-connect"
docker build -t kafka-connect:1.0.0 -f Dockerfile .

echo "===> start docker-compose"
docker-compose -f docker-compose.yml up -d
