package com.example.demo.factories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Slf4j
@Configuration
@ImportRuntimeHints(MyRuntimeHintsRegistrar.class)
class HintsConfiguration {

	@Bean
	ApplicationRunner felixApplicationRunner(Cat felix) {
		return args -> {
			log.info("felix says: ");
			felix.speak();
		};
	}

	@Bean
	CatFactoryBean felix() {
		return new CatFactoryBean();
	}

}
