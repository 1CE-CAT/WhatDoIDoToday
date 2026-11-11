package ru.maks.NauJava.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import ru.maks.NauJava.entity.Task;
import ru.maks.NauJava.entity.TaskStatus;

@RepositoryRestResource(path = "task") 
public interface TaskRepository extends CrudRepository<Task, Long>, TaskRepositoryCustom {

    List<Task> findByStatusAndDeadlineBefore(TaskStatus status, LocalDateTime date);

    @Query("SELECT t FROM Task t WHERE t.user.name = :userName")
    List<Task> findByUserName(@Param("userName") String userName);
}