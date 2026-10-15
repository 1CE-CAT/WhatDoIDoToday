package ru.maks.NauJava.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.maks.NauJava.entity.Task;
import ru.maks.NauJava.entity.TaskStatus;

public interface TaskRepository extends CrudRepository<Task, Long> {

    List<Task> findByStatusAndDeadlineBefore(TaskStatus status, LocalDateTime date);

    @Query("SELECT t FROM Task t WHERE t.user.name = :userName")
    List<Task> findByUserName(@Param("userName") String userName);
}