package com.intervueai.backend.auth.service;


import com.intervueai.backend.auth.dto.AuthResponse;
import com.intervueai.backend.auth.dto.LoginRequest;
import com.intervueai.backend.auth.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}