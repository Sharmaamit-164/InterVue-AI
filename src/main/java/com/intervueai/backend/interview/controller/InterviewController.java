package com.intervueai.backend.interview.controller;

import com.intervueai.backend.interview.dto.CreateInterviewRequest;
import com.intervueai.backend.interview.dto.InterviewQuestionResponse;
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

    public InterviewController(
            InterviewService interviewService
    ) {
        this.interviewService = interviewService;
    }

    @Operation(
            summary = "Create Interview",
            description = "Creates a new interview for the logged-in user."
    )
    @PostMapping
    public ResponseEntity<InterviewResponse> createInterview(
            @Valid @RequestBody CreateInterviewRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                interviewService.createInterview(
                        authentication.getName(),
                        request
                )
        );
    }

    @Operation(
            summary = "Get My Interviews",
            description = "Returns all interviews belonging to the logged-in user."
    )
    @GetMapping
    public ResponseEntity<List<InterviewResponse>> getMyInterviews(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                interviewService.getMyInterviews(
                        authentication.getName()
                )
        );
    }

    @Operation(
            summary = "Get Interview",
            description = "Returns a specific interview belonging to the logged-in user."
    )
    @GetMapping("/{interviewId}")
    public ResponseEntity<InterviewResponse> getMyInterview(
            @PathVariable Long interviewId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                interviewService.getMyInterview(
                        authentication.getName(),
                        interviewId
                )
        );
    }

    @Operation(
            summary = "Get Interview Questions",
            description = "Returns all questions and candidate answers for an interview."
    )
    @GetMapping("/{interviewId}/questions")
    public ResponseEntity<List<InterviewQuestionResponse>> getInterviewQuestions(
            @PathVariable Long interviewId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                interviewService.getInterviewQuestions(
                        authentication.getName(),
                        interviewId
                )
        );
    }

    @Operation(
            summary = "Complete Interview",
            description = "Marks the interview as completed."
    )
    @PutMapping("/{interviewId}/complete")
    public ResponseEntity<InterviewResponse> completeInterview(
            @PathVariable Long interviewId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                interviewService.completeInterview(
                        authentication.getName(),
                        interviewId
                )
        );
    }
}