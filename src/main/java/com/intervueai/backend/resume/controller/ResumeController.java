package com.intervueai.backend.resume.controller;

import com.intervueai.backend.resume.dto.ResumeResponse;
import com.intervueai.backend.resume.dto.ResumeUploadResponse;
import com.intervueai.backend.resume.service.ResumeService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    // Upload resume
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ResumeUploadResponse> uploadResume(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {

        String email = authentication.getName();

        ResumeUploadResponse response =
                resumeService.uploadResume(email, file);

        return ResponseEntity.ok(response);
    }

    // Get all resumes of logged-in user
    @GetMapping
    public ResponseEntity<List<ResumeResponse>> getMyResumes(
            Authentication authentication
    ) {

        String email = authentication.getName();

        List<ResumeResponse> resumes =
                resumeService.getMyResumes(email);

        return ResponseEntity.ok(resumes);
    }

    // Get a particular resume of logged-in user
    @GetMapping("/{resumeId}")
    public ResponseEntity<ResumeResponse> getMyResume(
            @PathVariable Long resumeId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        ResumeResponse response =
                resumeService.getMyResume(email, resumeId);

        return ResponseEntity.ok(response);
    }

    // Delete a resume
    @DeleteMapping("/{resumeId}")
    public ResponseEntity<String> deleteMyResume(
            @PathVariable Long resumeId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        resumeService.deleteMyResume(email, resumeId);

        return ResponseEntity.ok(
                "Resume deleted successfully"
        );
    }
}