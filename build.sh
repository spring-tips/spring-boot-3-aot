#!/usr/bin/env bash

./mvnw -DskipTests -Pnative clean package && ./target/demo
