package com.example.demo.hints;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.DecoratingProxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

@Slf4j
@Configuration
class CrmConfiguration {

	@Bean
	ApplicationRunner crmRunner(Crm crm) {
		return args -> crm.enroll(UUID.randomUUID().toString());
	}

	@Bean
	@ImportRuntimeHints(CrmHints.class)
	Crm crm() {
		var pfb = new ProxyFactoryBean();
		pfb.setTargetClass(Crm.class);
		pfb.addAdvice(new MethodInterceptor() {
			@Nullable
			@Override
			public Object invoke(@Nonnull MethodInvocation invocation) {
				if (invocation.getMethod().getName().equals("enroll"))
					log.info("enrolling " + invocation.getArguments()[0]);
				return null;
			}
		});
		return (Crm) pfb.getObject();
	}

	private static class CrmHints implements RuntimeHintsRegistrar {

		@Override
		public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
			hints.proxies().registerJdkProxy(builder -> builder.proxiedInterfaces(
					new Class<?>[] { Crm.class, SpringProxy.class, Advised.class, DecoratingProxy.class }));
		}

	}

}
