package com.intervueai.backend.user.dto;

public class UpdateUserRequest {

    private String email;

    public UpdateUserRequest() {
    }

    public UpdateUserRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}