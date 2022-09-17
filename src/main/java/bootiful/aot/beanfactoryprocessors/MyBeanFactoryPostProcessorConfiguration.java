package bootiful.aot.beanfactoryprocessors;

import lombok.extern.slf4j.Slf4j;
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

@Configuration
class MyBeanFactoryPostProcessorConfiguration {

	@Bean
	static MyBeanFactoryPostProcessor myBeanFactoryPostProcessor() {
		return new MyBeanFactoryPostProcessor();
	}

}

@Slf4j
class MyBeanFactoryPostProcessor implements BeanFactoryInitializationAotProcessor, BeanDefinitionRegistryPostProcessor {

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		var beanName = "mal";
		if (!registry.containsBeanDefinition(beanName)) {
			registry.registerBeanDefinition(beanName,
					BeanDefinitionBuilder.rootBeanDefinition(MessageApplicationListener.class).getBeanDefinition());
		}
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

	@Override
	public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
		return (context, code) -> context.getRuntimeHints().reflection().registerType(MessageApplicationListener.class,
				MemberCategory.values());
	}

}

@Slf4j
class MessageApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		log.info("this bean shouldn't be here!");
	}

}