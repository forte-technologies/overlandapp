package com.builtoverlander.trip_planner.controllers;

import com.builtoverlander.trip_planner.controllers.UserController;
import com.builtoverlander.trip_planner.dto.LoginRequest;
import com.builtoverlander.trip_planner.entities.User;
import com.builtoverlander.trip_planner.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(com.builtoverlander.trip_planner.config.SecurityConfig.class)

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testLoginUserSuccess() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        User mockUser = new User();
        mockUser.setUsername("testuser");

        when(userService.loginUser("testuser", "password123")).thenReturn(mockUser);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\", \"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userService, times(1)).loginUser("testuser", "password123");
    }

    @Test
    void testLoginUserInvalidCredentials() throws Exception {
        when(userService.loginUser("testuser", "wrongpassword"))
                .thenThrow(new RuntimeException("Invalid username or password"));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\", \"password\":\"wrongpassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid username or password"));

        verify(userService, times(1)).loginUser("testuser", "wrongpassword");
    }
}
