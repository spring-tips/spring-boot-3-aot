package com.example.demo.brap;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactoryBean;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Slf4j
class MethodLoggingBeanBuilder {

	static <T> T build(T t) {
		var pfb = new ProxyFactoryBean();
		pfb.setTarget(t);
		for (var i : t.getClass().getInterfaces())
			pfb.addInterface(i);
		pfb.addAdvice(new MethodInterceptor() {
			@Nullable
			@Override
			public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
				var methodName = invocation.getMethod().getName();
				log.info("beginning [" + methodName + "]");
				var result = invocation.proceed();
				log.info("finishing [" + methodName + "]");
				return result;
			}
		});

		var object = (T) pfb.getObject();
		for (var i : object.getClass().getInterfaces())
			log.info(i.getName());
		return object;
	}

}
