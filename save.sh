#!/usr/bin/env bash

./mvnw -DskipTests spring-javaformat:apply clean && git commit -am polish && git push
