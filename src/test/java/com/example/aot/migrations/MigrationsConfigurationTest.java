package com.example.aot.migrations;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

class MigrationsConfigurationTest {

	@Test
	void hints() throws Exception {

		var hints = new RuntimeHints();
		var registrar = new MigrationsConfiguration.MigrationsRuntimeHintsRegistrar();
		var classloader = getClass().getClassLoader();
		registrar.registerHints(hints, classloader);
		Assertions.assertThat(RuntimeHintsPredicates.resource().forResource("data.csv")).accepts(hints);

	}

}