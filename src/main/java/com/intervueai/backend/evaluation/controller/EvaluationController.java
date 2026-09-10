package com.intervueai.backend.evaluation.controller;

import com.intervueai.backend.evaluation.dto.EvaluationResponse;
import com.intervueai.backend.evaluation.service.EvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluations")
@SecurityRequirement(name = "bearerAuth")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @Operation(
            summary = "Generate interview evaluation",
            description = "Generates an AI-based evaluation for the authenticated user's interview."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evaluation generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid interview data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Interview not found")
    })
    @PostMapping("/generate/{interviewId}")
    public ResponseEntity<EvaluationResponse> generateEvaluation(
            @Parameter(description = "ID of the interview", example = "1")
            @PathVariable Long interviewId,
            Authentication authentication
    ) {
        String email = authentication.getName();

        EvaluationResponse response =
                evaluationService.generateEvaluation(email, interviewId);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get interview evaluation",
            description = "Retrieves the evaluation for the authenticated user's interview."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evaluation retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Evaluation or interview not found")
    })
    @GetMapping("/{interviewId}")
    public ResponseEntity<EvaluationResponse> getEvaluation(
            @Parameter(description = "ID of the interview", example = "1")
            @PathVariable Long interviewId,
            Authentication authentication
    ) {
        String email = authentication.getName();

        EvaluationResponse response =
                evaluationService.getEvaluation(email, interviewId);

        return ResponseEntity.ok(response);
    }
}