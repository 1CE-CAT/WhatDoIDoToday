package ru.maks.NauJava.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import ru.maks.NauJava.entity.Task;
import ru.maks.NauJava.entity.TaskStatus;
import ru.maks.NauJava.entity.User;

@Repository
public class TaskRepositoryCustomImpl implements TaskRepositoryCustom {

    private final EntityManager entityManager;

    @Autowired
    public TaskRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Task> findByStatusAndDeadlineBefore(TaskStatus status, LocalDateTime date) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Task> cq = cb.createQuery(Task.class);

        Root<Task> taskRoot = cq.from(Task.class);

        Predicate statusPredicate = cb.equal(taskRoot.get("status"), status);
        Predicate datePredicate = cb.lessThan(taskRoot.get("deadline"), date);

        cq.select(taskRoot).where(cb.and(statusPredicate, datePredicate));

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<Task> findByUserName(String userName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> cq = cb.createQuery(Task.class);
        Root<Task> taskRoot = cq.from(Task.class);

        Join<Task, User> userJoin = taskRoot.join("user", JoinType.INNER);

        Predicate namePredicate = cb.equal(userJoin.get("name"), userName);

        cq.select(taskRoot).where(namePredicate);
        return entityManager.createQuery(cq).getResultList();
    }
}