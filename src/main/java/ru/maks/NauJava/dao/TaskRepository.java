package ru.maks.NauJava.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.maks.NauJava.entity.Task;

@Component 
public class TaskRepository implements CrudRepository<Task, Long> {
    
    private final List<Task> taskContainer;

    @Autowired 
    public TaskRepository(List<Task> taskContainer) {
        this.taskContainer = taskContainer;
    }

    @Override
    public void create(Task entity) {
        taskContainer.add(entity);
    }

    @Override
    public Task read(Long id) {
        return taskContainer.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void update(Task entity) {
        Task existingTask = read(entity.getId());
        if (existingTask != null) {
            existingTask.setTitle(entity.getTitle());
            existingTask.setDescription(entity.getDescription());
            existingTask.setStatus(entity.getStatus());
            existingTask.setDeadline(entity.getDeadline());
            existingTask.setNotificationEnabled(entity.isNotificationEnabled());
        }
    }

    @Override
    public void delete(Long id) {
        Task taskToDelete = read(id);
        if (taskToDelete != null) {
            taskContainer.remove(taskToDelete);
        }
    }
    
}
