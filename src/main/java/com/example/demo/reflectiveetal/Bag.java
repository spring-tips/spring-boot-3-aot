package com.example.demo.reflectiveetal;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class Bag {

	private final String uuid = UUID.randomUUID().toString();

	public ID getUuid() {
		return new ID(this.uuid);
	}

}
