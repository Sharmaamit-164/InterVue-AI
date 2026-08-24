package com.intervueai.backend.user.service;

import com.intervueai.backend.exception.ResourceNotFoundException;
import com.intervueai.backend.user.dto.UpdateUserRequest;
import com.intervueai.backend.user.dto.UserResponse;
import com.intervueai.backend.user.entity.User;
import com.intervueai.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse getMyProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }

    @Override
    @Transactional
    public UserResponse updateMyProfile(
            String email,
            UpdateUserRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (request.getEmail() != null &&
                !request.getEmail().isBlank() &&
                !request.getEmail().equals(user.getEmail())) {

            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException(
                        "Email is already registered"
                );
            }

            user.setEmail(request.getEmail());
        }

        User updatedUser = userRepository.save(user);

        return new UserResponse(
                updatedUser.getId(),
                updatedUser.getEmail(),
                updatedUser.getRole()
        );
    }

    @Override
    @Transactional
    public void deleteMyAccount(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        userRepository.delete(user);
    }
}