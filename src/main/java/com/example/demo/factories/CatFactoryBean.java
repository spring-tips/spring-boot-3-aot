package com.example.demo.factories;

import org.springframework.beans.factory.FactoryBean;

class CatFactoryBean implements FactoryBean<Cat> {

	@Override
	public Cat getObject() throws Exception {
		return new Cat();
	}

	@Override
	public Class<?> getObjectType() {
		return Cat.class;
	}

}
