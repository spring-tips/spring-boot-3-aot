#!/usr/bin/env bash
rm -rf target && ./mvnw spring-javaformat:apply  -DskipTests -Pnative spring-javaformat:apply clean package && ./target/demo
