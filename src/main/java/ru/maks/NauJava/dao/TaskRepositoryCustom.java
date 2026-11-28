package ru.maks.NauJava.dao;

import java.time.LocalDateTime;
import java.util.List;

import ru.maks.NauJava.entity.Task;
import ru.maks.NauJava.entity.TaskStatus;

public interface TaskRepositoryCustom {

    List<Task> findByStatusAndDeadlineBefore(TaskStatus status, LocalDateTime date);

    List<Task> findByUserName(String userName);
}