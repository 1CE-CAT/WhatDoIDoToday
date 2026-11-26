package ru.maks.NauJava.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.maks.NauJava.dao.TaskRepository;
import ru.maks.NauJava.entity.Task;
import ru.maks.NauJava.entity.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/task")
public class TaskRestController {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskRestController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/search-by-status")
    public List<Task> findByStatusAndDate(
            @RequestParam TaskStatus status,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return taskRepository.findByStatusAndDeadlineBefore(status, date);
    }

    @GetMapping("/search-by-user")
    public List<Task> findByUserName(@RequestParam String userName) {
        return taskRepository.findByUserName(userName);
    }
}