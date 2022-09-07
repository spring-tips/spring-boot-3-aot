package com.example.demo.brap;

import org.springframework.aot.hint.annotation.Reflective;

@Reflective
class DefaultReverser implements Reverser {

	@Override
	public String reverse(String name) {
		return new StringBuilder(name).reverse().toString();
	}

}
