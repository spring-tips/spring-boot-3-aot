package com.example.demo.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.Optional;

@Slf4j
@ResponseBody
@Controller
class GreetingsController {

	@EventListener(ApplicationReadyEvent.class)
	public void ready() {
		log.info("application's ready!");
	}

	@GetMapping("/hello")
	Map<String, String> hello(@RequestParam Optional<String> name) {
		return name.map(nom -> Map.of("message", "hello, " + nom)).orElse(Map.of("message", "Hello, world!"));
	}

}
