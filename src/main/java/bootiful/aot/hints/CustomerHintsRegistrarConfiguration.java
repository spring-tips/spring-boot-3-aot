package bootiful.aot.hints;

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

@Slf4j
@Configuration
@ImportRuntimeHints(CustomerRuntimeHintsRegistrar.class)
class CustomerHintsRegistrarConfiguration {

	@Bean
	ApplicationListener<ApplicationReadyEvent> customerHintsRegistrarApplicationListener(ObjectMapper objectMapper) {
		return event -> log.info(getClass().getSimpleName() + ":" + json(new Customer(1, "Jane"), objectMapper));
	}

	@SneakyThrows
	private static String json(Object object, ObjectMapper objectMapper) {
		return objectMapper.writeValueAsString(object);
	}

}

class CustomerRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

	@Override
	public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
		hints.reflection().registerType(Customer.class, MemberCategory.values());
	}

}

record Customer(Integer id, String name) {
}