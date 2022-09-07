package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.reflections.Reflections;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.event.EventListener;
import org.springframework.core.DecoratingProxy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * things to try :
 */

@Controller
@Slf4j
@ImportResource("/context.xml")
@ResponseBody
@SpringBootApplication
@ImportRuntimeHints(MyRuntimeHintsRegistrar.class)
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @GetMapping("/hello")
    Map<String, String> hello(@RequestParam Optional<String> name) {
        return name.map(nom -> Map.of("message", "hello, " + nom)).orElse(Map.of("message", "Hello, world!"));
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ready() {
        log.info("application's ready!");
    }

    @Bean
    ApplicationRunner apple(@Apple Market market) {
        return args -> log.info(market.getClass().getName());
    }

    @Bean
    ApplicationRunner android(@Android Market market) {
        return args -> log.info(market.getClass().getName());
    }

    @Bean
    ApplicationRunner felixRunner(Cat felix) {
        return args -> {
            log.info("felix says: ");
            felix.speak();
        };
    }

    @Bean
    CatFactoryBean felix() {
        return new CatFactoryBean();
    }

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            var classNames = "com.example.demo.Dog,com.example.demo.Cat".split(",");
            for (var className : classNames) {
                var clazz = Class.forName(className);
                var method = clazz.getMethod("speak");
                var instance = clazz.getDeclaredConstructor().newInstance();
                method.invoke(instance);
            }
            var shape = Class.forName("com.example.demo.Square");
            var newShape = shape.getDeclaredConstructor().newInstance();
            var dimension = (Integer) shape.getMethod("getDimension").invoke(newShape);
            log.info("" + dimension);
        };
    }


    @Bean
    ApplicationRunner crmRunner(Crm crm) {
        return args -> crm.enroll(UUID.randomUUID().toString());
    }

    private static class CrmHints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.proxies().registerJdkProxy(builder -> builder
                    .proxiedInterfaces(new Class<?>[]{Crm.class, SpringProxy.class, Advised.class, DecoratingProxy.class}));
        }
    }

    @Bean
    @ImportRuntimeHints(CrmHints.class)
    Crm crm() {
        var pfb = new ProxyFactoryBean();
        pfb.setTargetClass(Crm.class);
        pfb.addAdvice(new MethodInterceptor() {
            @Nullable
            @Override
            public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
                if (invocation.getMethod().getName().equals("enroll"))
                    log.info("enrolling " + invocation.getArguments()[0]);
                return null;
            }
        });
        return (Crm) pfb.getObject();
    }

    @Bean
    CarFactoryBean carFactoryBean() {
        return new CarFactoryBean();
    }

    @Bean
    ApplicationRunner carRunner(Car car) {
        return args -> log.info("got a car " + car.getClass().getName());
    }

    @Bean
    MyBeanFactoryInitializationAotProcessor myBeanFactoryInitializationAotProcessor() {
        return new MyBeanFactoryInitializationAotProcessor();
    }
}


@Slf4j
@Configuration
class ReverserConfiguration {

    @Bean
    ApplicationListener<ApplicationReadyEvent> reverserReady(Reverser reverser) {
        return event -> {
            var reversed = reverser.reverse("hello");
            log.info("reversed: " + reversed);
        };
    }

    @Bean
    Reverser reverser() {
        return new DefaultReverser();
    }

    @Bean
    MethodLoggingBeanPostProcessor methodLoggingBeanPostProcessor() {
        return new MethodLoggingBeanPostProcessor();
    }

    @Bean
    MethodLoggingBeanRegistrationAotProcessor methodLoggingBeanRegistrationAotProcessor() {
        return new MethodLoggingBeanRegistrationAotProcessor();
    }
}


interface Reverser {

    String reverse(String name);
}

@Reflective
class DefaultReverser implements Reverser {

    @Override
    public String reverse(String name) {
        return new StringBuilder(name).reverse().toString();
    }
}

@Slf4j
class MethodLoggingBeanRegistrationAotProcessor implements BeanRegistrationAotProcessor {

    @Override
    public BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
        log.info("processAheadOfTime for " + registeredBean.getBeanClass(). getName());

        return (generationContext, beanRegistrationCode) -> {
            generationContext.getRuntimeHints().proxies().registerJdkProxy(
                    com.example.demo.Reverser.class,
                    org.springframework.aop.SpringProxy.class,
                    org.springframework.aop.framework.Advised.class,
                    org.springframework.core.DecoratingProxy.class);
        };
    }
}

@Slf4j
class MethodLoggingBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        log.info("found: " + beanName + " with class " + bean.getClass().getName());

        if (bean instanceof Reverser reverser) {
            log.info("found an instance of Reverser " + reverser);
            var result = MethodLoggingBeanBuilder.build(reverser);
            for (var o : result.getClass().getInterfaces())
                log.info("IF:" + o.getName());
            log.info("returning proxied instance of " + Reverser.class.getName());
            return result;
        }
        return bean;
    }

}

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


@Slf4j
class MyBeanFactoryInitializationAotProcessor implements BeanFactoryInitializationAotProcessor {

    @Override
    public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {

        var beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        for (var beanDefinitionName : beanDefinitionNames) {
            log.info("going to process the bean definition called " + beanDefinitionName);
            var bd = beanFactory.getBeanDefinition(beanDefinitionName);
            var clzz = bd.getBeanClassName();
            log.info("class is " + clzz);
        }
        return null;
    }
}


class Car {
}

class Sedan extends Car {
}

class Truck extends Car {
}

class CarFactoryBean implements FactoryBean<Object> {

    @Override
    public Object getObject() throws Exception {
        return Math.random() > 0.5 ? new Truck() : new Sedan();
    }

    @Override
    public Class<?> getObjectType() {
        return Car.class;
    }
}

interface Crm {

    void enroll(String id);
}

@Retention(RetentionPolicy.RUNTIME)
@Qualifier("android")
@interface Android {
}

@Retention(RetentionPolicy.RUNTIME)
@Qualifier("apple")
@interface Apple {
}

interface Market {
}

@Service
@Qualifier("android")
class GooglePlay implements Market {
}

@Service
@Qualifier("apple")
class AppStore implements Market {
}

class CatFactoryBean implements FactoryBean<Cat> {

    @Override
    public Cat getObject() throws Exception {
        return new Cat();
    }

    @Override
    public Class<?> getObjectType() {
        return Cat.class;
    }
}

@ResponseBody
@Controller
class BagController {

    private final Bag bag;

    BagController(Bag bag) {
        this.bag = bag;
    }

    @GetMapping("/bag")
    ID read() {
        return this.bag.getUuid();
    }
}

@Component
class Bag {

    private final String uuid = UUID.randomUUID().toString();

    public ID getUuid() {
        return new ID(this.uuid);
    }
}

@RegisterReflectionForBinding
record ID(String id) {
}

interface Animal {
    void speak();
}

@Slf4j
class Cat implements Animal {

    Cat(String name) {
        log.info("the name is " + name);
    }

    Cat() {
    }

    @Override
    public void speak() {
        log.info("meow!");
    }
}

@Slf4j
class Dog implements Animal {

    @Override
    public void speak() {
        log.info("woof!");
    }
}

class MessageProducer
        implements Supplier<String> {
    @Override
    public String get() {
        return "Hello, bean ref!";
    }
}

@Slf4j
class AnnouncingApplicationRunner implements ApplicationRunner {

    private final String message;

    private MessageProducer messageProducer;

    AnnouncingApplicationRunner(String message) {
        this.message = message;
    }

    AnnouncingApplicationRunner() {
        this.message = "Hello, world";
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (this.messageProducer != null) {
            log.info(this.messageProducer.get());
        } else log.info(this.message);
    }

    public void setMessageProducer(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }
}

interface Shape {
}

class Square implements Shape {

    public int getDimension() {
        return 1;
    }
}

@Slf4j
class MyRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

    private final Reflections reflections = new Reflections(Animal.class.getPackage().getName());

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        var subs = this.reflections.getSubTypesOf(Animal.class);
        for (var c : subs) {
            hints.reflection().registerType(c, MemberCategory.values());
            log.info("registering " + c.getName());
        }
    }
}
