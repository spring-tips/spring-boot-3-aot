package com.example.demo.scopes;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

@Scope(WebApplicationContext.SCOPE_REQUEST)
@Component
class Bag {

	private final String uuid = UUID.randomUUID().toString();

	public String getUuid() {
		return (this.uuid);
	}

}
