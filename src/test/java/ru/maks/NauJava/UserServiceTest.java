package ru.maks.NauJava;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import ru.maks.NauJava.dao.CommentRepository;
import ru.maks.NauJava.dao.TaskRepository;
import ru.maks.NauJava.dao.UserRepository;
import ru.maks.NauJava.entity.Comment;
import ru.maks.NauJava.entity.Task;
import ru.maks.NauJava.entity.TaskStatus;
import ru.maks.NauJava.entity.User;
import ru.maks.NauJava.service.UserService;

@SpringBootTest
class UserServiceTest {

    private final UserService userService;

    @SpyBean
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    public UserServiceTest(UserService userService) {
        this.userService = userService;
    }

    @Test
    void testDeleteUserAndRelatedData_Success() {
        User user = new User();
        user.setName("DeleteMe_" + UUID.randomUUID());
        user.setEmail(UUID.randomUUID() + "@test.com");
        user.setPassword("password");
        userRepository.save(user);

        Task task = new Task();
        task.setTitle("Задача для удаления");
        task.setStatus(TaskStatus.NEW);
        task.setDeadline(LocalDateTime.now().plusDays(1));
        task.setUser(user);
        taskRepository.save(task);

        Comment comment = new Comment();
        comment.setText("Комментарий для удаления");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setTask(task);
        commentRepository.save(comment);

        Long userId = user.getId();
        Long taskId = task.getId();
        Long commentId = comment.getId();

        userService.deleteUserAndRelatedData(userId);

        Assertions.assertTrue(userRepository.findById(userId).isEmpty(), "Пользователь не удален");
        Assertions.assertTrue(taskRepository.findById(taskId).isEmpty(), "Задача не удалена");
        Assertions.assertTrue(commentRepository.findById(commentId).isEmpty(), "Комментарий не удален");
    }

    @Test
    void testDeleteUserAndRelatedData_Rollback() {
        User user = new User();
        user.setName("RollbackUser_" + UUID.randomUUID());
        user.setEmail(UUID.randomUUID() + "@test.com");
        user.setPassword("password");
        userRepository.save(user);

        Task task = new Task();
        task.setTitle("Задача для отката");
        task.setStatus(TaskStatus.NEW);
        task.setUser(user);
        taskRepository.save(task);

        Comment comment = new Comment();
        comment.setText("Комментарий для отката");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setTask(task);
        commentRepository.save(comment);

        Long userId = user.getId();
        Long taskId = task.getId();
        Long commentId = comment.getId();

        Mockito.doThrow(new RuntimeException("Симуляция сбоя базы данных при удалении пользователя"))
               .when(userRepository).delete(Mockito.any(User.class));
        Assertions.assertThrows(RuntimeException.class, () -> {
            userService.deleteUserAndRelatedData(userId);
        });

        Assertions.assertTrue(userRepository.findById(userId).isPresent(), 
                "ОШИБКА: Пользователь был удален, хотя транзакция должна была откатиться!");
        Assertions.assertTrue(taskRepository.findById(taskId).isPresent(), 
                "ОШИБКА: Задача была удалена, хотя транзакция должна была откатиться!");
        Assertions.assertTrue(commentRepository.findById(commentId).isPresent(), 
                "ОШИБКА: Комментарий был удален, хотя транзакция должна была откатиться!");
    }
}