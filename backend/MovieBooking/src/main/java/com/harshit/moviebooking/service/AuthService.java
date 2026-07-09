package com.harshit.moviebooking.service;

import com.harshit.moviebooking.dto.auth.AuthResponse;
import com.harshit.moviebooking.dto.auth.LoginRequest;
import com.harshit.moviebooking.dto.auth.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

}