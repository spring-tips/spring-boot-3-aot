package com.example.demo.factories;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class Dog implements Animal {

	@Override
	public void speak() {
		log.info("woof!");
	}

}
