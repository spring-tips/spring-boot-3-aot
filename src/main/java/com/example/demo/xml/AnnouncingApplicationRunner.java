package com.example.demo.xml;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

@Slf4j
class AnnouncingApplicationRunner implements ApplicationRunner {

	private final String message;

	private MessageProducer messageProducer;

	AnnouncingApplicationRunner(String message) {
		this.message = message;
	}

	AnnouncingApplicationRunner() {
		this.message = "Hello, world";
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (this.messageProducer != null) {
			log.info(this.messageProducer.get());
		}
		else
			log.info(this.message);
	}

	public void setMessageProducer(MessageProducer messageProducer) {
		this.messageProducer = messageProducer;
	}

}
