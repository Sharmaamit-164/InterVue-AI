package com.intervueai.backend.resume.controller;

import com.intervueai.backend.resume.dto.ResumeResponse;
import com.intervueai.backend.resume.dto.ResumeUploadResponse;
import com.intervueai.backend.resume.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@SecurityRequirement(name = "bearerAuth")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    // =========================
    // Upload Resume
    // =========================

    @Operation(
            summary = "Upload Resume",
            description = "Upload a PDF resume for the logged-in user"
    )
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

    // =========================
    // Get My Resumes
    // =========================

    @Operation(
            summary = "Get My Resumes",
            description = "Get all resumes uploaded by the logged-in user"
    )
    @GetMapping
    public ResponseEntity<List<ResumeResponse>> getMyResumes(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                resumeService.getMyResumes(email)
        );
    }

    // =========================
    // Get Single Resume
    // =========================

    @Operation(
            summary = "Get My Resume",
            description = "Get a specific resume belonging to the logged-in user"
    )
    @GetMapping("/{resumeId}")
    public ResponseEntity<ResumeResponse> getMyResume(
            @PathVariable Long resumeId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                resumeService.getMyResume(email, resumeId)
        );
    }

    // =========================
    // Delete Resume
    // =========================

    @Operation(
            summary = "Delete My Resume",
            description = "Delete a resume belonging to the logged-in user"
    )
    @DeleteMapping("/{resumeId}")
    public ResponseEntity<Void> deleteMyResume(
            @PathVariable Long resumeId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        resumeService.deleteMyResume(email, resumeId);

        return ResponseEntity.noContent().build();
    }
}