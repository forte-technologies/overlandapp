package com.builtoverlander.trip_planner.services.UserServiceTest.java;

import com.builtoverlander.trip_planner.entities.User;
import com.builtoverlander.trip_planner.repositories.UserRepository;
import com.builtoverlander.trip_planner.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void testLoginUserSuccess() {
        String username = "testuser";
        String password = "password123";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword("hashedpassword");

        when(userRepository.findByUsername(username)).thenReturn(mockUser);
        when(passwordEncoder.matches(password, "hashedpassword")).thenReturn(true);

        User result = userService.loginUser(username, password);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, "hashedpassword");
    }

    @Test
    void testLoginUserInvalidUsername() {
        String username = "invaliduser";
        String password = "password123";

        when(userRepository.findByUsername(username)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.loginUser(username, password));

        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testLoginUserInvalidPassword() {
        String username = "testuser";
        String password = "wrongpassword";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword("hashedpassword");

        when(userRepository.findByUsername(username)).thenReturn(mockUser);
        when(passwordEncoder.matches(password, "hashedpassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.loginUser(username, password));

        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, "hashedpassword");
    }
}
