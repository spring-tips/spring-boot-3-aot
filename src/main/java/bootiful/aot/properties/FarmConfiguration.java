package bootiful.aot.properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@EnableConfigurationProperties(FarmProperties.class)
@Configuration
class FarmConfiguration {

	@Bean
	ApplicationListener<ApplicationReadyEvent> farmPropertiesRunner(FarmProperties properties) {
		return event -> log.info("farm properties name " + properties.name());
	}

}
