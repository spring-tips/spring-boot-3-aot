package com.example.demo;

import org.reflections.Reflections;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.Optional;

@Controller
@ResponseBody
@SpringBootApplication
@ImportRuntimeHints(MyRuntimeHintsRegistrar.class)
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @GetMapping("/hello")
    Map<String, String> hello(@RequestParam Optional<String> name) {
        return name
                .map(nom -> Map.of("message", "hello, " + nom))
                .orElse(Map.of("message", "Hello, world!"));
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
            var shape = Class.forName(Animal.class.getPackageName() + ".Square") ;
            var newShape = shape.getDeclaredConstructor().newInstance() ;

        };
    }
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

@Reflective
interface Shape {
}
@Reflective
class Circle implements Shape {}

@Reflective
//@RegisterReflectionForBinding
class Square implements Shape {

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
