#!/usr/bin/env bash

rm -rf target
./mvnw -DskipTests  spring-javaformat:apply clean package
./mvnw -DagentJvmArguments="-agentlib:native-image-agent=config-output-dir=target/native-image" spring-boot:run