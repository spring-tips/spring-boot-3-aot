package com.example.demo.javaconfig;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.util.UUID;

/* todo new B(a()) doesn't work yet */
@Configuration
class CrmJavaConfiguration {

	@Bean
	A a() {
		return new A();
	}

	@Bean
	B b() {
		return new B(this.a());
	}

}

@Slf4j
class A {

	@SneakyThrows
	A() {
		log.info("created A [" + UUID.randomUUID() + "]");
	}

}

@Slf4j
class B {

	B(A a) {
		log.info("created B");
		Assert.notNull(a, "the a is null");
	}

}