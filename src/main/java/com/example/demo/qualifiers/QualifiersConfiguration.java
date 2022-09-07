package com.example.demo.qualifiers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
class QualifiersConfiguration {

	@Bean
	ApplicationRunner apple(@Apple Market market) {
		return args -> log.info(market.getClass().getName());
	}

	@Bean
	ApplicationRunner android(@Android Market market) {
		return args -> log.info(market.getClass().getName());
	}

}
