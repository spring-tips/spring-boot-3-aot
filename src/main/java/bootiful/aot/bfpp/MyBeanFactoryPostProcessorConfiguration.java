package bootiful.aot.bfpp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.aot.BeanFactoryInitializationCode;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.BootstrapContext;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
			log.info("=====================");
			log.info("beanClassName: " + beanDefinition.getBeanClassName());
			log.info("rawClassName: " + beanDefinition.getResolvableType());
		}

	}

}

class MyBeanFactoryInitializationAotProcessor implements BeanFactoryInitializationAotProcessor {

	@Override
	public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory bf) {
		return (context, bic) -> context.getRuntimeHints().reflection().registerType(MessageApplicationListener.class);
	}

}

@Slf4j
class MessageApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		log.info("this bean shouldn't be here!");
	}

}