package com.example.aot.bfpp;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.UUID;

import static com.example.aot.bfpp.BfppConfiguration.BEAN_NAME;

@Configuration
class BfppConfiguration {

	// <1>
	static String BEAN_NAME = "myBfppListener";

	// <2>
	@Bean
	static ListenerBeanFactoryPostProcessor listenerBeanFactoryPostProcessor() {
		return new ListenerBeanFactoryPostProcessor();
	}

}

// <3>
class Listener implements ApplicationListener<ApplicationReadyEvent> {

	private final ObjectMapper objectMapper;

	Listener(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	@SneakyThrows
	public void onApplicationEvent(ApplicationReadyEvent event) {
		var products = List.of(new Product(UUID.randomUUID().toString()), new Product(UUID.randomUUID().toString()));
		for (var p : products)
			System.out.println(objectMapper.writeValueAsString(p));
	}

}

record Product(String sku) {
}

// <4>
class ListenerBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory bf) throws BeansException {
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		if (!registry.containsBeanDefinition(BEAN_NAME))
			registry.registerBeanDefinition(BEAN_NAME,
					BeanDefinitionBuilder.rootBeanDefinition("com.example.aot.bfpp.Listener").getBeanDefinition());
	}

}

// <5>
class ListenerBeanFactoryInitializationAotProcessor implements BeanFactoryInitializationAotProcessor {

	@Override
	public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory bf) {

		if (bf.containsBeanDefinition(BEAN_NAME)) {
			return (ctx, code) -> {
				var hints = ctx.getRuntimeHints();
				hints.reflection().registerType(Product.class, MemberCategory.values());
			};
		}
		return null;
	}

}
