package com.intervueai.backend.auth.service;

import com.intervueai.backend.auth.dto.AuthResponse;
import com.intervueai.backend.auth.dto.LoginRequest;
import com.intervueai.backend.auth.dto.RegisterRequest;
import com.intervueai.backend.auth.mapper.AuthMapper;
import com.intervueai.backend.security.JwtUtil;
import com.intervueai.backend.user.entity.User;
import com.intervueai.backend.user.repository.UserRepository;
import com.intervueai.backend.exception.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthMapper authMapper;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            AuthMapper authMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authMapper = authMapper;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        String cleanEmail = request.getEmail() == null ? "" : request.getEmail().trim().toLowerCase();
        if (cleanEmail.isEmpty()) {
            throw new BadRequestException("Email cannot be empty");
        }

        if (userRepository.existsByEmail(cleanEmail)) {
            throw new BadRequestException("Email already registered");
        }

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        User user = new User(
                cleanEmail,
                encodedPassword,
                "USER"
        );

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(
                savedUser.getEmail(),
                savedUser.getRole()
        );

        return authMapper.toAuthResponse(
                savedUser,
                token
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String cleanEmail = request.getEmail() == null ? "" : request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(cleanEmail)
                .or(() -> userRepository.findByEmail(request.getEmail()))
                .orElseThrow(() ->
                        new BadRequestException(
                                "Invalid email or password"
                        )
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new BadRequestException(
                    "Invalid email or password"
            );
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole()
        );

        return authMapper.toAuthResponse(
                user,
                token
        );
    }
}