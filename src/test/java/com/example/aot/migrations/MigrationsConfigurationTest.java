package com.example.aot.migrations;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

class MigrationsConfigurationTest {

	@Test
	void hints() {
		var hints = new RuntimeHints();
		// <1>
		var registrar = new MigrationsConfiguration.MigrationsRuntimeHintsRegistrar();
		var classloader = getClass().getClassLoader();
		// <2>
		registrar.registerHints(hints, classloader);
		// <3>
		Assertions.assertThat(RuntimeHintsPredicates.resource().forResource("data.csv")).accepts(hints);

	}

}