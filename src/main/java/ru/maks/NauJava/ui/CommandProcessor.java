package ru.maks.NauJava.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.maks.NauJava.entity.TaskStatus;
import ru.maks.NauJava.service.TaskService;

@Component 
public class CommandProcessor {
    private final TaskService taskService;

    @Autowired 
    public CommandProcessor(TaskService taskService) {
        this.taskService = taskService;
    }

    public void processCommand(String command) {
        String[] parts = command.split(" ");
        String action = parts[0];

        switch (action) {
            case "create":
                if (parts.length < 3) {
                    System.out.println("Usage: create <title> <description>");
                    return;
                }
                String title = parts[1];
                String description = parts[2];
                taskService.createTask(title, description);
                System.out.println("Task created.");
                break;

            case "find":
                if (parts.length < 2) {
                    System.out.println("Usage: find <id>");
                    return;
                }
                long idToFind = Long.parseLong(parts[1]);
                var task = taskService.findTaskById(idToFind);
                if (task != null) {
                    System.out.println("Task found: " + task);
                } else {
                    System.out.println("Task not found.");
                }
                break;

            case "delete":
                if (parts.length < 2) {
                    System.out.println("Usage: delete <id>");
                    return;
                }
                long idToDelete = Long.parseLong(parts[1]);
                taskService.deleteTaskById(idToDelete);
                System.out.println("Task deleted.");
                break;

            case "update":
                if (parts.length < 3) {
                    System.out.println("Usage: update <id> <status>");
                    return;
                }
                long idToUpdate = Long.parseLong(parts[1]);
                TaskStatus status = TaskStatus.valueOf(parts[2].toUpperCase());
                taskService.updateTaskById(idToUpdate, status);
                System.out.println("Task updated.");
                break;

            default:
                System.out.println("Unknown command.");
        }
    }
}
