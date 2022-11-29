package com.example.aot.factorybeans;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class FactoryBeansConfiguration {

	// <1>
	@Bean
	AnimalFactoryBean animalFactoryBean() {
		return new AnimalFactoryBean(true, false);
	}

	// <2>
	@Bean
	ApplicationListener<ApplicationReadyEvent> factoryBeanListener(Animal animal) {
		return event -> animal.speak();
	}

}

class AnimalFactoryBean implements FactoryBean<Animal> {

	private final boolean likesYarn, likesFrisbees;

	AnimalFactoryBean(boolean likesYarn, boolean likesFrisbees) {
		this.likesYarn = likesYarn;
		this.likesFrisbees = likesFrisbees;
	}

	@Override
	public Animal getObject() {
		return (this.likesYarn && !this.likesFrisbees) ? new Cat() : new Dog();
	}

	@Override
	public Class<?> getObjectType() {
		return Animal.class;
	}

}

interface Animal {

	void speak();

}

class Dog implements Animal {

	@Override
	public void speak() {
		System.out.println("woof");
	}

}

class Cat implements Animal {

	@Override
	public void speak() {
		System.out.println("meow");
	}

}