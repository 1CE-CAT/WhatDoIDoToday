package ru.maks.NauJava;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;
import ru.maks.NauJava.dao.TaskRepository;
import ru.maks.NauJava.dao.UserRepository;
import ru.maks.NauJava.entity.Task;
import ru.maks.NauJava.entity.TaskStatus;
import ru.maks.NauJava.entity.User;

@SpringBootTest
@Transactional 
class TaskRepositoryTest {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    TaskRepositoryTest(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Test
    void testFindByStatusAndDeadlineBefore() {
        User user = new User();
        user.setName("TestUser_" + UUID.randomUUID());
        user.setEmail(UUID.randomUUID() + "@test.com");
        user.setPassword("password");
        userRepository.save(user);

        Task task = new Task();
        task.setTitle("Просроченная задача");
        task.setStatus(TaskStatus.NEW);
        task.setDeadline(LocalDateTime.now().minusDays(1));
        task.setUser(user);
        taskRepository.save(task);

        List<Task> found = taskRepository.findByStatusAndDeadlineBefore(TaskStatus.NEW, LocalDateTime.now());

        Assertions.assertNotNull(found);
        Assertions.assertFalse(found.isEmpty());
        boolean containsOurTask = found.stream()
                .anyMatch(t -> t.getTitle().equals("Просроченная задача"));
        Assertions.assertTrue(containsOurTask, "Метод не нашел созданную задачу");
    }

    @Test
    void testFindByUserName() {
        String uniqueName = "UniqueUser_" + UUID.randomUUID();
        User user = new User();
        user.setName(uniqueName);
        user.setEmail(UUID.randomUUID() + "@test.com");
        user.setPassword("password");
        userRepository.save(user);

        Task task = new Task();
        task.setTitle("Задача для поиска по имени");
        task.setStatus(TaskStatus.NEW);
        task.setUser(user);
        taskRepository.save(task);

        List<Task> found = taskRepository.findByUserName(uniqueName);

        Assertions.assertNotNull(found);
        Assertions.assertEquals(1, found.size(), "Должна быть найдена ровно одна задача");
        Assertions.assertEquals("Задача для поиска по имени", found.getFirst().getTitle());
        Assertions.assertEquals(uniqueName, found.getFirst().getUser().getName());
    }


    @Test
    void testFindByStatusAndDeadlineBefore_CriteriaApi() {
        String uniqueTitle = "CriteriaTask_" + UUID.randomUUID();
        User user = new User();
        user.setName("CriteriaUser_" + UUID.randomUUID());
        user.setEmail(UUID.randomUUID() + "@test.com");
        user.setPassword("password");
        userRepository.save(user);

        Task task = new Task();
        task.setTitle(uniqueTitle);
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setDeadline(LocalDateTime.now().minusHours(5));
        task.setUser(user);
        taskRepository.save(task);

        List<Task> found = taskRepository.findByStatusAndDeadlineBefore(TaskStatus.IN_PROGRESS, LocalDateTime.now());
        Assertions.assertNotNull(found);
        boolean containsOurTask = found.stream()
                .anyMatch(t -> t.getTitle().equals(uniqueTitle));
        Assertions.assertTrue(containsOurTask);
    }

    @Test
    void testFindByUserName_CriteriaApi() {
        String uniqueName = "CriteriaUser_" + UUID.randomUUID();
        User user = new User();
        user.setName(uniqueName);
        user.setEmail(UUID.randomUUID() + "@test.com");
        user.setPassword("password");
        userRepository.save(user);

        Task task = new Task();
        task.setTitle("Задача для Criteria поиска");
        task.setStatus(TaskStatus.NEW);
        task.setUser(user);
        taskRepository.save(task);

        List<Task> found = taskRepository.findByUserName(uniqueName);

        Assertions.assertNotNull(found);
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals("Задача для Criteria поиска", found.getFirst().getTitle());
    }
}