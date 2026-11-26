package ru.maks.NauJava.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.maks.NauJava.dao.CommentRepository;
import ru.maks.NauJava.dao.TaskRepository;
import ru.maks.NauJava.dao.UserRepository;
import ru.maks.NauJava.entity.Role;
import ru.maks.NauJava.entity.Task;
import ru.maks.NauJava.entity.User;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           TaskRepository taskRepository,
                           CommentRepository commentRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void deleteUserAndRelatedData(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Пользователь с ID " + userId + " не найден");
        }
        User user = userOptional.get();
        List<Task> tasks = taskRepository.findByUserName(user.getName());
        for (Task task : tasks) {
            commentRepository.deleteAll(task.getComments());
        }
        taskRepository.deleteAll(tasks);
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    @Transactional
    public User addUser(User user) {
        if (userRepository.existsByName(user.getName())) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }
        return userRepository.save(user);
    }
}