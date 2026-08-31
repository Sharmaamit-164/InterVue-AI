package com.intervueai.backend.resume.service;

import com.intervueai.backend.resume.dto.ResumeResponse;
import com.intervueai.backend.resume.dto.ResumeUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResumeService {

    ResumeUploadResponse uploadResume(
            String email,
            MultipartFile file
    );

    List<ResumeResponse> getMyResumes(
            String email
    );

    ResumeResponse getMyResume(
            String email,
            Long resumeId
    );

    void deleteMyResume(
            String email,
            Long resumeId
    );
}