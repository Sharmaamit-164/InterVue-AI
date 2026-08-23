package com.intervueai.backend.auth.mapper;

import com.intervueai.backend.auth.dto.AuthResponse;
import com.intervueai.backend.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public AuthResponse toAuthResponse(
            User user,
            String token
    ) {
        return new AuthResponse(
                token,
                user.getEmail(),
                user.getRole()
        );
    }
}