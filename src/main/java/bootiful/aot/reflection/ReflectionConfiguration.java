package bootiful.aot.reflection;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Slf4j
@Configuration
class ReflectionConfiguration {

	@SneakyThrows
	private static String json(ObjectMapper objectMapper, Object o) {
		return objectMapper.writeValueAsString(o);
	}

	@Bean
	@RegisterReflectionForBinding(Customer.class)
	@ImportRuntimeHints(CustomerServiceRuntimeHints.class)
	ApplicationListener<ApplicationReadyEvent> reflectionListener(CustomerService customerService,
			ObjectMapper objectMapper) {
		return event -> {
			var customer = customerService.getCustomerById(2);
			var json = json(objectMapper, customer);
			log.info("customer data loaded from .csv: " + json);
		};
	}

}

class CustomerServiceRuntimeHints implements RuntimeHintsRegistrar {

	@Override
	public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
		hints.resources().registerPattern("data.csv");
	}

}

@Service
class CustomerService implements InitializingBean {

	private final Resource resource = new ClassPathResource("data.csv");

	private final Map<Integer, Customer> db = new ConcurrentHashMap<>();

	Customer getCustomerById(Integer id) {
		return this.db.getOrDefault(id, null);
	}

	@Override
	@SneakyThrows
	public void afterPropertiesSet() {
		try (var fin = new InputStreamReader(this.resource.getInputStream())) {
			var string = FileCopyUtils.copyToString(fin);
			Stream.of(string.split(System.lineSeparator()))//
					.map(line -> {
						var cols = Stream.of(line.split(",")).map(String::trim).toList();
						return new Customer(Integer.parseInt(cols.get(0)), cols.get(1));
					}) //
					.forEach(c -> this.db.put(c.id(), c));
		}

	}

}

record Customer(Integer id, String name) {
}
