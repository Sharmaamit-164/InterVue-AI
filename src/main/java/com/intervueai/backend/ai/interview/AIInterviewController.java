package com.intervueai.backend.ai.interview;

import com.intervueai.backend.interview.dto.AIInterviewResponse;
import com.intervueai.backend.interview.dto.CandidateAnswerRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/interview")
@SecurityRequirement(name = "bearerAuth")
public class AIInterviewController {

    private final AIInterviewService aiInterviewService;

    public AIInterviewController(
            AIInterviewService aiInterviewService
    ) {
        this.aiInterviewService = aiInterviewService;
    }

    /*
     * ============================================================
     * EXISTING STANDALONE AI ENDPOINTS
     * ============================================================
     */

    @Operation(
            summary = "Generate First AI Interview Question",
            description = "Generates the first interview question dynamically using the logged-in user's resume and selected job."
    )
    @PostMapping("/first-question")
    public ResponseEntity<String> generateFirstQuestion(
            @RequestParam Long resumeId,
            @RequestParam Long jobId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        String question =
                aiInterviewService.generateFirstQuestion(
                        email,
                        resumeId,
                        jobId
                );

        return ResponseEntity.ok(question);
    }

    @Operation(
            summary = "Generate Next AI Interview Question",
            description = "Generates the next interview question dynamically using the resume, job, and previous interview conversation."
    )
    @PostMapping("/next-question")
    public ResponseEntity<String> generateNextQuestion(
            @RequestParam Long resumeId,
            @RequestParam Long jobId,
            @RequestParam(
                    required = false,
                    defaultValue = ""
            ) String previousConversation,
            Authentication authentication
    ) {

        String email = authentication.getName();

        String question =
                aiInterviewService.generateNextQuestion(
                        email,
                        resumeId,
                        jobId,
                        previousConversation
                );

        return ResponseEntity.ok(question);
    }

    /*
     * ============================================================
     * ACTUAL INTERVIEW ENDPOINTS
     * ============================================================
     */

    @Operation(
            summary = "Generate First Question For Interview",
            description = "Generates the first AI question for an existing interview and saves it in the database."
    )
    @PostMapping("/{interviewId}/first-question")
    public ResponseEntity<AIInterviewResponse> generateFirstQuestionForInterview(
            @PathVariable Long interviewId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        AIInterviewResponse question =
                aiInterviewService.generateFirstQuestionForInterview(
                        email,
                        interviewId
                );

        return ResponseEntity.ok(question);
    }

    @Operation(
            summary = "Answer Current Question And Generate Next",
            description = "Saves the candidate's answer and generates the next AI question. The interview automatically completes after question 10."
    )
    @PostMapping("/{interviewId}/next-question")
    public ResponseEntity<AIInterviewResponse> generateNextQuestionForInterview(
            @PathVariable Long interviewId,
            @Valid @RequestBody CandidateAnswerRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        AIInterviewResponse result =
                aiInterviewService.generateNextQuestionForInterview(
                        email,
                        interviewId,
                        request.getCandidateAnswer()
                );

        return ResponseEntity.ok(result);
    }
}