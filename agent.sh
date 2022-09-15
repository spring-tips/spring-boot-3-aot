#!/usr/bin/env bash

rm -rf target
./mvnw -DskipTests  spring-javaformat:apply clean package

# i created a property (agentJvmArguments) in the build that's "" by default, then i overrode it when running the script.
./mvnw -DagentJvmArguments="-agentlib:native-image-agent=config-output-dir=target/native-image" spring-boot:run