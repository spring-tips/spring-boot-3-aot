package com.example.aot.components;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Configuration
class ComponentsConfiguration {

    @EventListener(ApplicationReadyEvent.class)
    public void hello() {
        System.out.println("hello, world!");
    }
}

@Controller
@ResponseBody
class GreetingsController {

    @GetMapping("/hello/{name}")
    Map<String, String> hello(@PathVariable String name) {
        return Map.of("message", "Hello, " + name + "!");
    }

}
