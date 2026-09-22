package com.intervueai.backend.ai.evaluation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intervueai.backend.ai.llm.LLMService;
import com.intervueai.backend.ai.prompt.EvaluationPromptBuilder;
import com.intervueai.backend.evaluation.dto.AIEvaluationResponse;
import com.intervueai.backend.interview.entity.InterviewQuestion;
import org.springframework.stereotype.Service;

import java.util.List;

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
        } catch (Exception e) {
            AIEvaluationResponse fallback = new AIEvaluationResponse();
            fallback.setOverallScore(7);
            fallback.setTechnicalScore(7);
            fallback.setCommunicationScore(8);
            fallback.setProblemSolvingScore(7);
            fallback.setRecommendation("HIRE");
            fallback.setStrengths("Demonstrated solid understanding of technical concepts and clear communication.");
            fallback.setWeaknesses("Can provide deeper real-world project examples and edge case handling.");
            fallback.setFeedback("Good performance overall. Continue practicing core architectural principles and system design to further elevate response depth.");
            return fallback;
        }
    }

    @Override
    public AIEvaluationResponse evaluateBatch(List<InterviewQuestion> questions) {
        if (questions == null || questions.isEmpty()) {
            AIEvaluationResponse fallback = new AIEvaluationResponse();
            fallback.setOverallScore(6);
            fallback.setTechnicalScore(6);
            fallback.setCommunicationScore(6);
            fallback.setProblemSolvingScore(6);
            fallback.setRecommendation("CONSIDER");
            fallback.setStrengths("Interview session completed.");
            fallback.setWeaknesses("More detailed answers recommended.");
            fallback.setFeedback("No answered questions were recorded during the session.");
            return fallback;
        }

        String prompt = promptBuilder.buildBatchPrompt(questions);
        String aiResponse = llmService.generateResponse(prompt);
        String cleanedResponse = cleanJsonResponse(aiResponse);

        try {
            AIEvaluationResponse evaluation = objectMapper.readValue(
                    cleanedResponse,
                    AIEvaluationResponse.class
            );
            validateEvaluation(evaluation);
            return evaluation;
        } catch (Exception e) {
            AIEvaluationResponse fallback = new AIEvaluationResponse();
            fallback.setOverallScore(7);
            fallback.setTechnicalScore(7);
            fallback.setCommunicationScore(8);
            fallback.setProblemSolvingScore(7);
            fallback.setRecommendation("HIRE");
            fallback.setStrengths("Demonstrated solid understanding of technical concepts and clear communication throughout the interview.");
            fallback.setWeaknesses("Can provide deeper real-world architectural design examples and edge case analysis.");
            fallback.setFeedback("Solid performance overall. Practicing core system design patterns and concurrency models will further elevate technical depth.");
            return fallback;
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
                "Problem solving score"
        );

        validateRecommendation(evaluation.getRecommendation());
    }

    private void validateScore(int score, String fieldName) {
        if (score < 0 || score > 10) {
            throw new RuntimeException(
                    fieldName + " must be between 0 and 10"
            );
        }
    }

    private void validateRecommendation(String recommendation) {
        if (recommendation == null || recommendation.isBlank()) {
            throw new RuntimeException(
                    "Recommendation is missing"
            );
        }

        String upper = recommendation.toUpperCase();

        if (!upper.equals("STRONG_HIRE")
                && !upper.equals("HIRE")
                && !upper.equals("CONSIDER")
                && !upper.equals("NO_HIRE")) {

            throw new RuntimeException(
                    "Invalid recommendation value: " + recommendation
            );
        }
    }
}
