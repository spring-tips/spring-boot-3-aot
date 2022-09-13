#!/usr/bin/env bash

rm -rf target
SPRING_AOT_ENABLED=true ./mvnw -DskipTests  spring-javaformat:apply clean package spring-boot:run