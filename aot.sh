#!/usr/bin/env bash

rm -rf target
export SPRING_AOT_ENABLED=true
./mvnw -DskipTests -Pnative -Dspring-boot.run.jvmArguments="-Dspring.aot.enabled=true" clean package spring-boot:run