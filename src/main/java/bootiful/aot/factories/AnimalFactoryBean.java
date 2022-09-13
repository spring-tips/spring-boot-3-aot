package bootiful.aot.factories;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

class AnimalFactoryBean implements FactoryBean<Animal> {

	@Override
	public Animal getObject() {
		return Math.random() > .5 ? new Dog() : new Cat();
	}

	@Override
	public Class<?> getObjectType() {
		return Animal.class;
	}

}

@Slf4j
final class Dog implements Animal {

	@Override
	public void speak() {
		log.info("woof!");
	}

}

@Slf4j
final class Cat implements Animal {

	Cat(String name) {
		log.info("the name is " + name);
	}

	Cat() {
	}

	@Override
	public void speak() {
		log.info("meow!");
	}

}

sealed interface Animal permits Cat, Dog {

	void speak();

}

@Slf4j
@Configuration
class HintsConfiguration {

	@Slf4j
	static class MyRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

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

	@Bean
	ApplicationRunner animalApplicationRunner(Animal felix) {
		return args -> felix.speak();
	}

	@Bean
	@ImportRuntimeHints(MyRuntimeHintsRegistrar.class)
	AnimalFactoryBean animal() {
		return new AnimalFactoryBean();
	}

}
