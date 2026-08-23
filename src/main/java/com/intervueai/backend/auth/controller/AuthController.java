package com.intervueai.backend.auth.controller;

import com.intervueai.backend.auth.dto.AuthResponse;
import com.intervueai.backend.auth.dto.LoginRequest;
import com.intervueai.backend.auth.dto.RegisterRequest;
import com.intervueai.backend.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request
    ) {

        AuthResponse response =
                authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request
    ) {

        AuthResponse response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }
}