package com.intervueai.backend.interview.controller;

import com.intervueai.backend.interview.dto.CreateInterviewRequest;
import com.intervueai.backend.interview.dto.InterviewResponse;
import com.intervueai.backend.interview.service.InterviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@SecurityRequirement(name = "bearerAuth")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @Operation(
            summary = "Create Interview",
            description = "Creates a new interview session using the logged-in user's selected resume and job."
    )
    @PostMapping
    public ResponseEntity<InterviewResponse> createInterview(
            @Valid @RequestBody CreateInterviewRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        InterviewResponse response =
                interviewService.createInterview(email, request);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get My Interviews",
            description = "Returns all interview sessions belonging to the logged-in user."
    )
    @GetMapping
    public ResponseEntity<List<InterviewResponse>> getMyInterviews(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                interviewService.getMyInterviews(email)
        );
    }

    @Operation(
            summary = "Get My Interview",
            description = "Returns one interview session belonging to the logged-in user."
    )
    @GetMapping("/{interviewId}")
    public ResponseEntity<InterviewResponse> getMyInterview(
            @PathVariable Long interviewId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                interviewService.getMyInterview(
                        email,
                        interviewId
                )
        );
    }

    @Operation(
            summary = "Complete Interview",
            description = "Marks the selected interview session as completed."
    )
    @PutMapping("/{interviewId}/complete")
    public ResponseEntity<InterviewResponse> completeInterview(
            @PathVariable Long interviewId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                interviewService.completeInterview(
                        email,
                        interviewId
                )
        );
    }
}