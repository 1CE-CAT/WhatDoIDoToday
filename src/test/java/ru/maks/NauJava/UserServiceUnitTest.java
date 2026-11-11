package ru.maks.NauJava;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.maks.NauJava.dao.CommentRepository;
import ru.maks.NauJava.dao.TaskRepository;
import ru.maks.NauJava.dao.UserRepository;
import ru.maks.NauJava.entity.User;
import ru.maks.NauJava.service.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void deleteUserAndRelatedData_UserNotFound_ThrowsException() {
        Long userId = 99L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUserAndRelatedData(userId);
        });
    }
    @Test
    void deleteUserAndRelatedData_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("TestUser");
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(taskRepository.findByUserName("TestUser")).thenReturn(java.util.Collections.emptyList());
        userService.deleteUserAndRelatedData(userId);
        Mockito.verify(userRepository, Mockito.times(1)).delete(user);
    }
}