#!/usr/bin/env bash

./mvnw -DskipTests -Pnative spring-javaformat:apply clean package && ./target/demo
