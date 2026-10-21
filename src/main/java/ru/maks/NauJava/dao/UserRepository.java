package ru.maks.NauJava.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import ru.maks.NauJava.entity.User;

@RepositoryRestResource(path = "User") 
public interface UserRepository extends CrudRepository<User, Long> {
}