package com.example.demo.reflection;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

class CustomerService {

	private final String[] names = "Yuxin,Olga,Dr. Syer,Stéphane,Jürgen,Andy,Violetta".split(",");

	Customer getCustomerById(Integer id) {
		var index = new Random().nextInt(this.names.length);
		var orders = IntStream.range(0, (int) (Math.random() * 10))//
				.mapToObj(i -> new Order(i, id, UUID.randomUUID().toString())) //
				.toList();
		return new Customer(id, this.names[index], orders);
	}

}

@Slf4j
@Configuration
class ReflectionConfiguration {

	@Bean
	@ImportRuntimeHints(ReflectionRuntimeHints.class)
	CustomerService customerService() {
		return new CustomerService();
	}

	@Bean
	ApplicationListener<ApplicationReadyEvent> reflectionListener(CustomerService customerService,
			ObjectMapper objectMapper) {
		return event -> {
			var customer = customerService.getCustomerById(2);
			var json = json(objectMapper, customer);
			log.info(json);
		};
	}

	static class ReflectionRuntimeHints implements RuntimeHintsRegistrar {

		@Override
		public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
			hints.reflection().registerType(Customer.class, MemberCategory.values());
			hints.reflection().registerType(Order.class, MemberCategory.values());
		}

	}

	@SneakyThrows
	private static String json(ObjectMapper objectMapper, Object o) {
		return objectMapper.writeValueAsString(o);
	}

}

record Customer(Integer id, String name, List<Order> orders) {
}

record Order(Integer id, Integer customerId, String sku) {
}