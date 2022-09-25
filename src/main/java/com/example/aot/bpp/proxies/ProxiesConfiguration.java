package com.example.aot.bpp.proxies;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

@Configuration
class ProxiesConfiguration {

    @Bean
    ApplicationListener<ApplicationReadyEvent> loggedListener(OrderService service) {
        return event -> service.addToPrices(7);
    }

    @Bean
    LoggedBeanPostProcessor loggedBeanPostProcessor() {
        return new LoggedBeanPostProcessor();
    }
}

class LoggedBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor {

    private static boolean matches(Class<?> clazzName) {
        return clazzName != null && clazzName.getAnnotation(Logged.class) != null;
    }

    private static ProxyFactory proxyFactory(Object target, Class<?> targetClass) {
        var pf = new ProxyFactory();
        pf.setTargetClass(targetClass);
        pf.setInterfaces(targetClass.getInterfaces());
        pf.setProxyTargetClass(true);
        pf.addAdvice((MethodInterceptor) invocation -> {
            var methodName = invocation.getMethod().getName();
            System.out.println("before " + methodName);
            var result = invocation.getMethod().invoke(target, invocation.getArguments());
            System.out.println("after " + methodName);
            return result;
        });
        if (null != target) {
            pf.setTarget(target);
        }
        return pf;
    }

    @Override
    public Class<?> determineBeanType(Class<?> beanClass, String beanName) throws BeansException {
        if (matches(beanClass)) {
            return proxyFactory(null, beanClass).getProxyClass(beanClass.getClassLoader());
        }
        return beanClass;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        var beanClass = bean.getClass();
        if (matches(beanClass)) {
            return proxyFactory(bean, beanClass).getProxy(beanClass.getClassLoader());
        }
        return bean;
    }
}

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@interface Logged {
}

@Logged
@Service
class OrderService {

    void addToPrices(double amount) {
        System.out.println("adding $" + amount);
    }
}
