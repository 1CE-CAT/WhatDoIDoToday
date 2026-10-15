package ru.maks.NauJava.dao;

import org.springframework.data.repository.CrudRepository;

import ru.maks.NauJava.entity.Comment;

public interface CommentRepository extends CrudRepository<Comment, Long> {
}