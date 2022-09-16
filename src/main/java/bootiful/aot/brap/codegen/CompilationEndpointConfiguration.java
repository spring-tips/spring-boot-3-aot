package bootiful.aot.brap.codegen;

import bootiful.aot.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
class CompilationEndpointConfiguration {

	@Bean
	CompilationEndpoint compilationEndpoint() {
		return new CompilationEndpoint();
	}

	@Bean
	CompilationAotProcessor compilationAotProcessor() {
		return new CompilationAotProcessor();
	}

	@Bean
	ApplicationListener<ApplicationReadyEvent> compilationEndpointListener(CompilationEndpoint endpoint) {
		return event -> log.info(Utils.toString(endpoint.compilation()));
	}

}
