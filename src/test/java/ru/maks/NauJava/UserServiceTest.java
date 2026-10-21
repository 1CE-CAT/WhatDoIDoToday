package ru.maks.NauJava;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;

    @Autowired
    UserServiceTest(UserService userService,
                    UserRepository userRepository,
                    TaskRepository taskRepository,
                    CommentRepository commentRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
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
        User controlUser = new User();
        controlUser.setName("ControlUser_" + UUID.randomUUID());
        controlUser.setEmail(UUID.randomUUID() + "@test.com");
        controlUser.setPassword("password");
        userRepository.save(controlUser);

        Task controlTask = new Task();
        controlTask.setTitle("Контрольная задача");
        controlTask.setStatus(TaskStatus.NEW);
        controlTask.setUser(controlUser);
        taskRepository.save(controlTask);

        Long controlUserId = controlUser.getId();
        Long controlTaskId = controlTask.getId();

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUserAndRelatedData(-1L);
        });
        Assertions.assertTrue(userRepository.findById(controlUserId).isPresent(),
                "Контрольный пользователь был удален, хотя не должен был");
        Assertions.assertTrue(taskRepository.findById(controlTaskId).isPresent(),
                "Контрольная задача была удалена, хотя не должна была");
    }
}