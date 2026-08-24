package com.intervueai.backend.user.service;

import com.intervueai.backend.user.dto.UpdateUserRequest;
import com.intervueai.backend.user.dto.UserResponse;

public interface UserService {

    UserResponse getMyProfile(String email);

    UserResponse updateMyProfile(String email, UpdateUserRequest request);

    void deleteMyAccount(String email);
}