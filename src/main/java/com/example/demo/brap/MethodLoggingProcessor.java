package com.example.demo.brap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * This is a special case of a bean that has both runtime and compile time characteristics
 */
@Slf4j
class MethodLoggingProcessor implements BeanPostProcessor, BeanRegistrationAotProcessor {

	/**
	 * If this isn't set to false, then the default assumption is that the
	 * {@link BeanRegistrationAotProcessor} will 'replace' at compile time the effects of
	 * the runtime code in the BeanPostProcessor. In this case, I'm not replacing the
	 * proxies. just configuring hints for them, so I do *not* want the default behavior
	 */
	@Override
	public boolean isBeanExcludedFromAotProcessing() {
		return false;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof Reverser reverser) {
			log.info("found an instance of Reverser " + reverser);
			return MethodLoggingBeanBuilder.build(reverser);
		}
		return bean;
	}

	@Override
	public BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
		return (generationContext, beanRegistrationCode) -> generationContext.getRuntimeHints().proxies()
				.registerJdkProxy(Reverser.class, org.springframework.aop.SpringProxy.class,
						org.springframework.aop.framework.Advised.class,
						org.springframework.core.DecoratingProxy.class);
	}

}
