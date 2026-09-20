package com.sentinelops.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinelops.dto.AuthResponse;
import com.sentinelops.dto.LoginRequest;
import com.sentinelops.dto.RegisterRequest;
import com.sentinelops.dto.UserDto;
import com.sentinelops.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void login_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin@sentinelops.dev", "password123");

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("admin@sentinelops.dev");
        userDto.setFullName("Admin User");
        userDto.setRole("ROLE_ADMIN");

        AuthResponse authResponse = new AuthResponse("mock-jwt-token", userDto);
        when(authService.login(any(LoginRequest.class), anyString())).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.user.email").value("admin@sentinelops.dev"))
                .andExpect(jsonPath("$.user.role").value("ROLE_ADMIN"));
    }

    @Test
    void register_Success() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("New Dev", "dev@sentinelops.dev", "password123");

        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setEmail("dev@sentinelops.dev");
        userDto.setFullName("New Dev");
        userDto.setRole("ROLE_DEVELOPER");

        AuthResponse authResponse = new AuthResponse("mock-jwt-token-2", userDto);
        when(authService.register(any(RegisterRequest.class), anyString())).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token-2"))
                .andExpect(jsonPath("$.user.email").value("dev@sentinelops.dev"))
                .andExpect(jsonPath("$.user.role").value("ROLE_DEVELOPER"));
    }
}
