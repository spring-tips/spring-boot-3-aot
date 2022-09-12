#!/usr/bin/env bash

./mvnw -U -DskipTests -Pnative spring-javaformat:apply clean package && ./target/demo
