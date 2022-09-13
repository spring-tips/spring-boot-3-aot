package bootiful.aot.qualifiers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
class QualifiersConfiguration {

	@Bean
	ApplicationRunner apple(@Apple Market market) {
		return args -> debug(market);
	}

	@Bean
	ApplicationRunner android(@Android Market market) {
		return args -> debug(market);
	}

	private static void debug(Market market) {
		log.info(market.getClass().getName());
	}

}
