package com.example.aot.basics;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.CrudRepository;

import java.util.stream.Stream;

@Configuration
class BasicsConfiguration {

    @Bean
    ApplicationListener<ApplicationReadyEvent> basicsApplicationListener(
            CustomerRepository repository) {
        return event -> repository //
                .saveAll(Stream.of("A", "B", "C").map(name -> new Customer(null, name)).toList()) //
                .forEach(System.out::println);
    }
}

// <1>
record Customer(@Id Integer id, String name) {
}

// <2>
interface CustomerRepository extends CrudRepository<Customer, Integer> {
}