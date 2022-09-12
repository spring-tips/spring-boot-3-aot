package com.example.demo.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class EventListeningComponent {

	@EventListener(ApplicationReadyEvent.class)
	public void ready() {
		log.info("application's ready!");
	}

}
