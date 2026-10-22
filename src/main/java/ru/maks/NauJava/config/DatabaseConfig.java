package ru.maks.NauJava.config;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import ru.maks.NauJava.entity.Task;

@Configuration 
public class DatabaseConfig {

    @Bean
    @Scope(value = "singleton")
    public List<Task> taskContainer() {
        return new ArrayList<>();
    }
}
