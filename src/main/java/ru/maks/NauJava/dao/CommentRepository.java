package ru.maks.NauJava.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import ru.maks.NauJava.entity.Comment;

@RepositoryRestResource(path="comment")  
public interface CommentRepository extends CrudRepository<Comment, Long> {
}