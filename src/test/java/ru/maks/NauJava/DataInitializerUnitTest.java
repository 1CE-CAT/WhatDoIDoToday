package ru.maks.NauJava;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.maks.NauJava.config.DataInitializer;
import ru.maks.NauJava.dao.UserRepository;
import ru.maks.NauJava.entity.Role;
import ru.maks.NauJava.entity.User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private final DataInitializer dataInitializer = new DataInitializer();

    @Test
    void initDatabase_AdminDoesNotExist_CreatesAdmin() throws Exception {
        when(userRepository.existsByName("admin")).thenReturn(false);
        when(passwordEncoder.encode("admin123")).thenReturn("encodedAdminPass");

        CommandLineRunner runner = dataInitializer.initDatabase(userRepository, passwordEncoder);
        runner.run();

        verify(userRepository, times(1)).save(argThat((User u) ->
                "admin".equals(u.getName()) &&
                "admin@example.com".equals(u.getEmail()) &&
                "encodedAdminPass".equals(u.getPassword()) &&
                u.getRole() == Role.ADMIN
        ));
    }

    @Test
    void initDatabase_AdminAlreadyExists_DoesNotCreateDuplicate() throws Exception {
        when(userRepository.existsByName("admin")).thenReturn(true);

        CommandLineRunner runner = dataInitializer.initDatabase(userRepository, passwordEncoder);
        runner.run();

        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }
}