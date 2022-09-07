package com.example.demo.bfiap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class BeanFactoryInitializationAotProcessorConfiguration {

	@Bean
	MyBeanFactoryInitializationAotProcessor myBeanFactoryInitializationAotProcessor() {
		return new MyBeanFactoryInitializationAotProcessor();
	}

}
