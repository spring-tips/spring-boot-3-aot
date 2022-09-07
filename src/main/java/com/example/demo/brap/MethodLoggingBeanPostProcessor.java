package com.example.demo.brap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Slf4j
class MethodLoggingBeanPostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

		log.info("found: " + beanName + " with class " + bean.getClass().getName());

		if (bean instanceof Reverser reverser) {
			log.info("found an instance of Reverser " + reverser);
			var result = MethodLoggingBeanBuilder.build(reverser);
			for (var o : result.getClass().getInterfaces())
				log.info("IF:" + o.getName());
			log.info("returning proxied instance of " + Reverser.class.getName());
			return result;
		}
		return bean;
	}

}
