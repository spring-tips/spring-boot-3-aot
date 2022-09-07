package com.example.demo.factories;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class Cat implements Animal {

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
