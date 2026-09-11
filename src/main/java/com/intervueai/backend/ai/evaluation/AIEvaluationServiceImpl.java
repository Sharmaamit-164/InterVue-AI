package com.intervueai.backend.ai.evaluation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.intervueai.backend.ai.llm.LLMService;
import com.intervueai.backend.ai.prompt.EvaluationPromptBuilder;
import com.intervueai.backend.evaluation.dto.AIEvaluationResponse;
import org.springframework.stereotype.Service;

@Service
public class AIEvaluationServiceImpl implements AIEvaluationService {

    private final LLMService llmService;
    private final EvaluationPromptBuilder promptBuilder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AIEvaluationServiceImpl(
            LLMService llmService,
            EvaluationPromptBuilder promptBuilder
    ) {
        this.llmService = llmService;
        this.promptBuilder = promptBuilder;
    }

    @Override
    public AIEvaluationResponse evaluate(
            String question,
            String answer
    ) {

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException(
                    "Interview question cannot be null or empty"
            );
        }

        if (answer == null || answer.isBlank()) {
            throw new IllegalArgumentException(
                    "Candidate answer cannot be null or empty"
            );
        }

        String prompt = promptBuilder.buildPrompt(
                question,
                answer
        );

        String aiResponse = llmService.generateResponse(prompt);

        String cleanedResponse = cleanJsonResponse(aiResponse);

        try {

            AIEvaluationResponse evaluation =
                    objectMapper.readValue(
                            cleanedResponse,
                            AIEvaluationResponse.class
                    );

            validateEvaluation(evaluation);

            return evaluation;

        } catch (JsonProcessingException e) {

            throw new RuntimeException(
                    "Failed to parse AI evaluation response: "
                            + aiResponse,
                    e
            );
        }
    }

    private String cleanJsonResponse(String response) {

        if (response == null || response.isBlank()) {
            throw new RuntimeException(
                    "AI returned an empty evaluation response"
            );
        }

        String cleaned = response.trim();

        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7).trim();
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3).trim();
        }

        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(
                    0,
                    cleaned.length() - 3
            ).trim();
        }

        int start = cleaned.indexOf("{");
        int end = cleaned.lastIndexOf("}");

        if (start >= 0 && end > start) {
            cleaned = cleaned.substring(
                    start,
                    end + 1
            );
        }

        return cleaned;
    }

    private void validateEvaluation(
            AIEvaluationResponse evaluation
    ) {

        if (evaluation == null) {
            throw new RuntimeException(
                    "AI evaluation response is null"
            );
        }

        validateScore(
                evaluation.getOverallScore(),
                "Overall score"
        );

        validateScore(
                evaluation.getTechnicalScore(),
                "Technical score"
        );

        validateScore(
                evaluation.getCommunicationScore(),
                "Communication score"
        );

        validateScore(
                evaluation.getProblemSolvingScore(),
                "Problem-solving score"
        );

        if (evaluation.getRecommendation() == null
                || evaluation.getRecommendation().isBlank()) {

            throw new RuntimeException(
                    "AI evaluation recommendation is missing"
            );
        }

        evaluation.setRecommendation(
                evaluation.getRecommendation()
                        .trim()
                        .toUpperCase()
        );
    }

    private void validateScore(
            Integer score,
            String fieldName
    ) {

        if (score == null) {
            throw new RuntimeException(
                    fieldName
                            + " is missing from AI response"
            );
        }

        if (score < 0 || score > 10) {
            throw new RuntimeException(
                    fieldName
                            + " must be between 0 and 10"
            );
        }
    }
}

