package ru.maks.NauJava.service;

import java.util.Optional;

import ru.maks.NauJava.entity.User;

public interface UserService {
    void deleteUserAndRelatedData(Long userId);

    Optional<User> findByName(String name);

    User addUser(User user);
}