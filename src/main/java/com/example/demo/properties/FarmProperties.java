package com.example.demo.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bootiful")
record FarmProperties(String name) {
}