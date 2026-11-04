package ru.maks.NauJava.dao;

import org.springframework.data.repository.CrudRepository;

import ru.maks.NauJava.entity.User;

public interface UserRepository extends CrudRepository<User, Long> {
}