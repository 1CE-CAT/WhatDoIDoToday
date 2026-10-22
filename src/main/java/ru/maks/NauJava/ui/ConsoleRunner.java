package ru.maks.NauJava.ui;

import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsoleRunner {

    private final CommandProcessor commandProcessor;

    public ConsoleRunner(CommandProcessor commandProcessor) {
        this.commandProcessor = commandProcessor;
    }

    @Bean
    public CommandLineRunner commandScanner() {
        return args -> {
            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    System.out.print("Enter command: ");
                    if (!scanner.hasNextLine()) {
                        break;
                    }
                    String command = scanner.nextLine().trim();
                    if (command.isEmpty()) {
                        continue;
                    }
                    if ("exit".equalsIgnoreCase(command)) {
                        System.out.println("Exiting...");
                        break;
                    }
                    commandProcessor.processCommand(command);
                }
            }
        };
    }
}