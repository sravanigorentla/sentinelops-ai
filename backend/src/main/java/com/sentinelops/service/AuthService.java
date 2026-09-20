package com.sentinelops.service;

import com.sentinelops.domain.entity.Role;
import com.sentinelops.domain.entity.User;
import com.sentinelops.domain.enums.RoleName;
import com.sentinelops.dto.AuthResponse;
import com.sentinelops.dto.LoginRequest;
import com.sentinelops.dto.RegisterRequest;
import com.sentinelops.dto.UserDto;
import com.sentinelops.exception.BadRequestException;
import com.sentinelops.repository.RoleRepository;
import com.sentinelops.repository.UserRepository;
import com.sentinelops.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuditLogService auditLogService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider,
                       AuditLogService auditLogService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.auditLogService = auditLogService;
    }

    public AuthResponse login(LoginRequest loginRequest, String ipAddress) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        auditLogService.logAction(user.getId(), user.getEmail(), "USER_LOGIN", "USER", user.getId().toString(), "User logged in", ipAddress);

        return new AuthResponse(jwt, new UserDto(user));
    }

    public AuthResponse register(RegisterRequest registerRequest, String ipAddress) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        // Server-side role assignment: If no users exist, make first user ADMIN, else DEVELOPER
        RoleName defaultRoleName = userRepository.count() == 0 ? RoleName.ROLE_ADMIN : RoleName.ROLE_DEVELOPER;
        Role userRole = roleRepository.findByName(defaultRoleName)
                .orElseGet(() -> roleRepository.save(new Role(null, defaultRoleName)));

        User user = new User(
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getFullName(),
                userRole
        );

        User savedUser = userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(registerRequest.getEmail(), registerRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        auditLogService.logAction(savedUser.getId(), savedUser.getEmail(), "USER_REGISTER", "USER", savedUser.getId().toString(), "New user registered with role " + defaultRoleName, ipAddress);

        return new AuthResponse(jwt, new UserDto(savedUser));
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserDto::new).toList();
    }
}
