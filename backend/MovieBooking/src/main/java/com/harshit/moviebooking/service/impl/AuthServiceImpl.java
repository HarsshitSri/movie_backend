package com.harshit.moviebooking.service.impl;

import com.harshit.moviebooking.dto.auth.AuthResponse;
import com.harshit.moviebooking.dto.auth.LoginRequest;
import com.harshit.moviebooking.dto.auth.RegisterRequest;
import com.harshit.moviebooking.repository.RoleRepo;
import com.harshit.moviebooking.repository.UserRepo;
import com.harshit.moviebooking.security.JwtService;
import com.harshit.moviebooking.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.harshit.moviebooking.entity.Role;
import com.harshit.moviebooking.entity.User;
import com.harshit.moviebooking.enums.AccountStatus;
import com.harshit.moviebooking.enums.RoleName;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepository;
    private final RoleRepo roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepo userRepository,
                           RoleRepo roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        Role role = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        User user = new User();

        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setDateOfBirth(request.getDateOfBirth());
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser.getEmail());

        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }
}