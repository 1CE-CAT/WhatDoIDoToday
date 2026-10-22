package ru.maks.NauJava.service;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import ru.maks.NauJava.config.AppConfig;
import ru.maks.NauJava.dao.TaskRepository;
import ru.maks.NauJava.entity.Task;
import ru.maks.NauJava.entity.TaskStatus;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final AppConfig appConfig;

    private final AtomicLong idGenerator = new AtomicLong(1L);

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, AppConfig appConfig) {
        this.taskRepository = taskRepository;
        this.appConfig = appConfig;
    }

    @PostConstruct
    public void init() {
        System.out.println("Приложение: " + appConfig.getAppName());
        System.out.println("Версия: " + appConfig.getAppVersion());
    }

    @Override
    public void createTask(String title, String description) {
        Task task = new Task();
        task.setId(idGenerator.getAndIncrement());
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(TaskStatus.NEW);
        taskRepository.create(task);
    }

    @Override
    public Task findTaskById(long id) {
        return taskRepository.read(id);
    }

    @Override
    public void deleteTaskById(long id) {
        taskRepository.delete(id);
    }

    @Override
    public void updateTaskById(long id, TaskStatus status) {
        Task task = taskRepository.read(id);
        if (task != null) {
            task.setStatus(status);
            taskRepository.update(task);
        }
    }
}