package com.example.aot.properties;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DemoProperties.class)
class PropertiesConfiguration {

    @Bean
    ApplicationListener<ApplicationReadyEvent> propertiesApplicationListener( //<1>
            DemoProperties properties) {
        return args -> System.out.println("the name is " + properties.name());
    }
}

// <2>
@ConfigurationProperties(prefix = "bootiful")
record DemoProperties(String name) {
}
