package ru.maks.NauJava.dao;

import org.springframework.data.repository.CrudRepository;

import ru.maks.NauJava.entity.Tag;

public interface TagRepository extends CrudRepository<Tag, Long> {
}