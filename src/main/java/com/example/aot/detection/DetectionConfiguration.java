package com.example.aot.detection;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.NativeDetector;

@Configuration
class DetectionConfiguration {

    @Bean
    ApplicationListener<ApplicationReadyEvent> detectionListener() {
        return args -> System.out.println("is native image? " + NativeDetector.inNativeImage());
    }
}
