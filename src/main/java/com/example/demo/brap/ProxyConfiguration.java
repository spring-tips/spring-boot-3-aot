package com.example.demo.brap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
class ProxyConfiguration {

	@Bean
	ApplicationListener<ApplicationReadyEvent> reverserReady(Reverser reverser) {
		return event -> {
			var reversed = reverser.reverse("hello");
			log.info("reversed: " + reversed);
		};
	}

	@Bean
	Reverser reverser() {
		return new DefaultReverser();
	}

	@Bean
	MethodLoggingBeanPostProcessor methodLoggingBeanPostProcessor() {
		return new MethodLoggingBeanPostProcessor();
	}

	@Bean
	MethodLoggingBeanRegistrationAotProcessor methodLoggingBeanRegistrationAotProcessor() {
		return new MethodLoggingBeanRegistrationAotProcessor();
	}

}
