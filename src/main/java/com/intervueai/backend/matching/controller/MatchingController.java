package com.intervueai.backend.matching.controller;

import com.intervueai.backend.matching.dto.MatchingResponse;
import com.intervueai.backend.matching.service.MatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matching")
@SecurityRequirement(name = "bearerAuth")
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    // =========================
    // Resume - Job Matching
    // =========================

    @Operation(
            summary = "Match Resume With Job",
            description = "Compare the logged-in user's resume with a selected job"
    )
    @PostMapping("/resume/{resumeId}/job/{jobId}")
    public ResponseEntity<MatchingResponse> matchResumeWithJob(
            @PathVariable Long resumeId,
            @PathVariable Long jobId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        MatchingResponse response =
                matchingService.matchResumeWithJob(
                        email,
                        resumeId,
                        jobId
                );

        return ResponseEntity.ok(response);
    }
}