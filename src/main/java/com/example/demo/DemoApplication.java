package com.example.demo;

import org.reflections.Reflections;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Controller
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
        System.out.println("application's ready!");
    }

    @Bean
    ApplicationRunner apple(@Apple Market market) {
        return args -> System.out.println(market.getClass().getName());
    }

    @Bean
    ApplicationRunner android(@Android Market market) {
        return args -> System.out.println(market.getClass().getName());
    }

    @Bean
    ApplicationRunner felixRunner(Cat felix) {
        return args -> {
            System.out.println("felix says: ");
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
            System.out.println(dimension);
        };
    }
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

class Cat implements Animal {

    @Override
    public void speak() {
        System.out.println("meow!");
    }
}

class Dog implements Animal {

    @Override
    public void speak() {
        System.out.println("woof!");
    }
}

class MessageProducer
        implements Supplier<String> {
    @Override
    public String get() {
        return "Hello, bean ref!";
    }
}

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
        if (this.messageProducer != null)
            System.out.println(this.messageProducer.get());
        else System.out.println(this.message);
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

class MyRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

    private final Reflections reflections = new Reflections(Animal.class.getPackage().getName());

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        var subs = this.reflections.getSubTypesOf(Animal.class);
        for (var c : subs) {
            hints.reflection().registerType(c, MemberCategory.values());
            System.out.println("registering " + c.getName());
        }
    }
}
