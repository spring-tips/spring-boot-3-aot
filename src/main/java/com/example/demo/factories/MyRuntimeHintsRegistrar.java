package com.example.demo.factories;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

@Slf4j
class MyRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

	private final Reflections reflections = new Reflections(Animal.class.getPackage().getName());

	@Override
	public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
		var subs = this.reflections.getSubTypesOf(Animal.class);
		for (var c : subs) {
			hints.reflection().registerType(c, MemberCategory.values());
			log.info("registering " + c.getName());
		}
	}

}
