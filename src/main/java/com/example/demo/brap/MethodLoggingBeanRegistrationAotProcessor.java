package com.example.demo.brap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.support.RegisteredBean;

@Slf4j
class MethodLoggingBeanRegistrationAotProcessor implements BeanRegistrationAotProcessor {

	@Override
	public BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
		log.info("processAheadOfTime for " + registeredBean.getBeanClass().getName());

		return (generationContext, beanRegistrationCode) -> {
			generationContext.getRuntimeHints().proxies().registerJdkProxy(Reverser.class,
					org.springframework.aop.SpringProxy.class, org.springframework.aop.framework.Advised.class,
					org.springframework.core.DecoratingProxy.class);
		};
	}

}
