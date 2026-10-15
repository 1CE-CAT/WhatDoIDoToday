package ru.maks.NauJava.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.maks.NauJava.dao.CommentRepository;
import ru.maks.NauJava.dao.TaskRepository;
import ru.maks.NauJava.dao.UserRepository;
import ru.maks.NauJava.entity.Task;
import ru.maks.NauJava.entity.User;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           TaskRepository taskRepository,
                           CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
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
}