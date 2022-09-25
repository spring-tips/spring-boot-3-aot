package com.example.aot.migrations;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.util.stream.Stream;

@Configuration
@RegisterReflectionForBinding(Person.class)
class MigrationsConfiguration {

    @Bean
    @ImportRuntimeHints(MigrationsRuntimeHintsRegistrar.class)
    ApplicationListener<ApplicationReadyEvent> peopleListener(
            ObjectMapper objectMapper,
            @Value("classpath:/data.csv") Resource csv) {
        return new ApplicationListener<ApplicationReadyEvent>() {
            @SneakyThrows
            @Override
            public void onApplicationEvent(ApplicationReadyEvent event) {
                try (var in = new InputStreamReader(csv.getInputStream())) {
                    var csvData = FileCopyUtils.copyToString(in);
                    Stream.of(csvData.split(System.lineSeparator()))
                            .map(line -> line.split(","))
                            .map(row -> new Person(row[0], row[1]))
                            .map(person -> json(person, objectMapper))
                            .forEach(System.out::println);
                }

            }
        };
    }

    @SneakyThrows
    private static String json(Person person, ObjectMapper objectMapper) {
        return objectMapper.writeValueAsString(person);
    }

    static class MigrationsRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
//        hints.reflection().registerType(Person.class, MemberCategory.values());
            hints.resources().registerPattern("data.csv");
        }
    }
}


record Person(String id, String name) {
}