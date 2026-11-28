package ru.maks.NauJava;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.maks.NauJava.dao.UserRepository;
import ru.maks.NauJava.entity.Role;
import ru.maks.NauJava.entity.User;
import ru.maks.NauJava.service.UserDetailsServiceImpl;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetailsWithRole() {
        User user = new User();
        user.setName("maks");
        user.setPassword("{bcrypt}hashed");
        user.setRole(Role.USER);

        when(userRepository.findByName("maks")).thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername("maks");

        Assertions.assertEquals("maks", details.getUsername());
        Assertions.assertEquals("{bcrypt}hashed", details.getPassword());
        Assertions.assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsUsernameNotFoundException() {
        when(userRepository.findByName("unknown")).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown"));
    }
}