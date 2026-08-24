package com.intervueai.backend.user.controller;

import com.intervueai.backend.user.dto.UpdateUserRequest;
import com.intervueai.backend.user.dto.UserResponse;
import com.intervueai.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(
            Authentication authentication) {

        String email = authentication.getName();

        UserResponse response =
                userService.getMyProfile(email);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(
            Authentication authentication,
            @RequestBody UpdateUserRequest request) {

        String email = authentication.getName();

        UserResponse response =
                userService.updateMyProfile(email, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(
            Authentication authentication) {

        String email = authentication.getName();

        userService.deleteMyAccount(email);

        return ResponseEntity.noContent().build();
    }
}