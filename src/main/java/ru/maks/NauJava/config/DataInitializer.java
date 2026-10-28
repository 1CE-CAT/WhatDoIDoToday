package ru.maks.NauJava.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import ru.maks.NauJava.dao.UserRepository;
import ru.maks.NauJava.entity.Role;
import ru.maks.NauJava.entity.User;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            boolean adminExists = userRepository.existsByName("admin");

            if (!adminExists) {
                User admin = new User();
                admin.setName("admin");
                admin.setEmail("admin@example.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
                System.out.println("Создан администратор: логин 'admin', пароль 'admin123'");
            }
        };
    }
}