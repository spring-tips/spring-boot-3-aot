package com.example.aot.events;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
class EventsConfiguration {

    // <1>
    @Bean
    ApplicationListener<WebServerInitializedEvent> webServerInitializedEventApplicationListener() {
        return event -> run("ApplicationListener<WebServerInitializedEvent>", event);
    }

    // <2>
    @EventListener
    public void eventListener(ApplicationReadyEvent event) {
        run("@EventListener", event);
    }

    private void run(String where, ApplicationEvent are) {
        System.out.println(where + " : " + are);
    }
}
