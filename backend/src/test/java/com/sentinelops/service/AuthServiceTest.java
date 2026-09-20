package com.sentinelops.service;

import com.sentinelops.domain.entity.Role;
import com.sentinelops.domain.entity.User;
import com.sentinelops.domain.enums.RoleName;
import com.sentinelops.dto.AuthResponse;
import com.sentinelops.dto.RegisterRequest;
import com.sentinelops.repository.RoleRepository;
import com.sentinelops.repository.UserRepository;
import com.sentinelops.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuthService authService;

    private Role devRole;

    @BeforeEach
    void setUp() {
        devRole = new Role(2L, RoleName.ROLE_DEVELOPER);
    }

    @Test
    void testRegisterUser_Success_EnforcesServerSideRole() {
        RegisterRequest request = new RegisterRequest("Jane Doe", "jane@sentinelops.io", "Password123!");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.count()).thenReturn(1L); // Non-zero count -> ROLE_DEVELOPER
        when(roleRepository.findByName(RoleName.ROLE_DEVELOPER)).thenReturn(Optional.of(devRole));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");
        
        User savedUser = new User(request.getEmail(), "hashedPassword", request.getFullName(), devRole);
        savedUser.setId(10L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(tokenProvider.generateToken(auth)).thenReturn("mocked-jwt-token");

        AuthResponse response = authService.register(request, "127.0.0.1");

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        assertEquals("jane@sentinelops.io", response.getUser().getEmail());
        assertEquals("ROLE_DEVELOPER", response.getUser().getRole());

        verify(userRepository, times(1)).save(any(User.class));
    }
}
