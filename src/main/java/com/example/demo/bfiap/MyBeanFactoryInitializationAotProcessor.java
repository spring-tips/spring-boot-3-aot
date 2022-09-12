package com.example.demo.bfiap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aot.generate.GeneratedMethod;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.javapoet.CodeBlock;

import javax.lang.model.element.Modifier;

@Slf4j
class MyBeanFactoryInitializationAotProcessor implements BeanFactoryInitializationAotProcessor {

	@Override
	public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
		return (ctx, code) -> {
			var generatedMethod = code.getMethods().add("registerStuff", (method) -> {
				method.addJavadoc("Add ImportAwareBeanPostProcessor to support ImportAware beans");
				method.addModifiers(Modifier.STATIC, Modifier.PUBLIC);
				method.addParameter(DefaultListableBeanFactory.class, "beanFactory");
				method.addCode(generateAddPostProcessorCode());
			});

			code.addInitializer(generatedMethod.toMethodReference());
		};
	}

	private static CodeBlock generateAddPostProcessorCode() {
		return CodeBlock.builder() //
				.addStatement( //
						"""
								        System.out.println("Hello, world!")
								""") //
				.build();
	}

}
