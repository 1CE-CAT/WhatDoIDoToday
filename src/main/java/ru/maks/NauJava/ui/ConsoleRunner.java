package ru.maks.NauJava.ui;

import java.util.Scanner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration 
public class ConsoleRunner {
    
    private CommandProcessor commandProcessor;

    @Value("${app.name:Default App Name}")
    private String appName;

    @Value("${app.version:0.0.0}")
    private String appVersion;


    @Bean 
    public CommandLineRunner commandScanner() {
        return args -> {
            System.out.println("Запуск приложения: " + appName);
            System.out.println("Версия: " + appVersion);
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Enter command: ");
                String command = scanner.nextLine();
                commandProcessor.processCommand(command);
            }
        };
    }
}
