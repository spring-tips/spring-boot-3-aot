package com.example.demo.bfiap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

@Slf4j
class MyBeanFactoryInitializationAotProcessor implements BeanFactoryInitializationAotProcessor {

	@Override
	public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {

		var beanDefinitionNames = beanFactory.getBeanDefinitionNames();
		for (var beanDefinitionName : beanDefinitionNames) {
			log.info("going to process the bean definition called " + beanDefinitionName);
			var bd = beanFactory.getBeanDefinition(beanDefinitionName);
			var clzz = bd.getBeanClassName();
			log.info("class is " + clzz);
		}
		return null;
	}

}
