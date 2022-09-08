package com.example.demo.brap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Slf4j
class MethodLoggingBeanPostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof Reverser reverser) {
			log.info("found an instance of Reverser " + reverser);
			return MethodLoggingBeanBuilder.build(reverser);
		}
		return bean;
	}

}
