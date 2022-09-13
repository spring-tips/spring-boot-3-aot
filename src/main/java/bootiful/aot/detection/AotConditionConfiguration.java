package bootiful.aot.detection;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aot.AotDetector;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.NativeDetector;
import org.springframework.core.SpringProperties;
import org.springframework.core.env.Environment;

import java.util.Map;

@Slf4j
@Configuration
class AotConditionConfiguration {

	@Bean
	ApplicationListener<ApplicationReadyEvent> isNativeApplicationListener(Environment env) {
		return event -> {
			var map = Map.of(//
					"aot enabled (Environment)", "" + (env.getProperty(AotDetector.AOT_ENABLED)), //
					"aot enabled?", SpringProperties.getFlag(AotDetector.AOT_ENABLED), //
					"isNativeImage", Boolean.toString(NativeDetector.inNativeImage()), //
					"AotDetector", Boolean.toString(AotDetector.useGeneratedArtifacts()), //
					"org.graalvm.nativeimage.imagecode", "" + System.getProperty("org.graalvm.nativeimage.imagecode"), //
					"org.graalvm.nativeimage.imagecode (Environment)",
					"" + env.getProperty("org.graalvm.nativeimage.imagecode") //
			);
			map.forEach((k, v) -> log.info(k + '=' + v));

		};
	}

}
