package ru.maks.NauJava.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import ru.maks.NauJava.entity.Tag;

@RepositoryRestResource(path = "tag") 
public interface TagRepository extends CrudRepository<Tag, Long> {
}