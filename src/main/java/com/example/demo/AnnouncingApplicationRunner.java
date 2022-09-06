package com.example.demo;

import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

@Reflective
class AnnouncingApplicationRunner implements ApplicationRunner {

    private final String message;

    public AnnouncingApplicationRunner(String message) {
        this.message = message;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(message);
    }
}
