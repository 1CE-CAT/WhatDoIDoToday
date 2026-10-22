package ru.maks.NauJava.service;
import ru.maks.NauJava.entity.Task;
import ru.maks.NauJava.entity.TaskStatus;

public interface TaskService {
    void createTask(String title, String description);
    Task findTaskById(long id);
    void deleteTaskById(long id);
    void updateTaskById(long id, TaskStatus status);
}
