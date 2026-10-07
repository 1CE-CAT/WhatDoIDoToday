package ru.maks.NauJava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.maks.NauJava.dao.TaskRepository;
import ru.maks.NauJava.entity.Task;
import ru.maks.NauJava.entity.TaskStatus;

@Service 
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    @Autowired 
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void createTask(String title, String description) {
        Task task = new Task();
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
