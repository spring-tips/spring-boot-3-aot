#!/usr/bin/env bash
mvn spring-javaformat:apply && ./blog.sh  && git commit -am polish && git push
