#!/usr/bin/env bash

rm -rf target
./mvnw -Dagent=true -DskipTests  spring-javaformat:apply clean package spring-boot:run