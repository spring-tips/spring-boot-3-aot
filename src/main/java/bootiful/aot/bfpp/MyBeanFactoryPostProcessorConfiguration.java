package bootiful.aot.bfpp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.lang.model.element.Modifier;

import static org.springframework.javapoet.CodeBlock.builder;

@Configuration
class MyBeanFactoryPostProcessorConfiguration {

	@Bean
	MyBeanFactoryInitializationAotProcessor myBeanFactoryInitializationAotProcessor() {
		return new MyBeanFactoryInitializationAotProcessor();
	}

	@Bean
	static MyBeanFactoryPostProcessor myBeanFactoryPostProcessor() {
		return new MyBeanFactoryPostProcessor();
	}

}

@Slf4j
class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

		if (beanFactory instanceof BeanDefinitionRegistry bdr) {
			var beanName = "bdr";
			if (bdr.containsBeanDefinition(beanName)) {
				bdr.removeBeanDefinition(beanName);
			} //
			bdr.registerBeanDefinition("bdr",
					BeanDefinitionBuilder.rootBeanDefinition(MessageApplicationListener.class).getBeanDefinition());
		} //

		for (var beanName : beanFactory.getBeanDefinitionNames()) {
			var beanDefinition = beanFactory.getBeanDefinition(beanName);
			log.debug("=====================");
			log.debug("beanClassName: " + beanDefinition.getBeanClassName());
			log.debug("rawClassName: " + beanDefinition.getResolvableType());
		}

	}

}

class MyBeanFactoryInitializationAotProcessor implements BeanFactoryInitializationAotProcessor {

	@Override
	public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory bf) {
		return (context, code) -> {
			// hints
			context.getRuntimeHints().reflection().registerType(MessageApplicationListener.class,
					MemberCategory.values());

			// code generation
			var className = MyBeanFactoryInitializationAotProcessor.class.getName();
			var codeBlock = builder().addStatement("System.out.println(\"Hello, world from $L!\")  ", className)
					.build();
			var generatedMethod = code//
					.getMethods() //
					.add("registerPrintln", (method) -> {
						method.addJavadoc("Register a println()");
						method.addModifiers(Modifier.STATIC, Modifier.PUBLIC);
						method.addParameter(DefaultListableBeanFactory.class, "beanFactory");
						method.addCode(codeBlock);
					});
			code.addInitializer(generatedMethod.toMethodReference());
		};
	}

}

@Slf4j
class MessageApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		log.info("this bean shouldn't be here!");
	}

}