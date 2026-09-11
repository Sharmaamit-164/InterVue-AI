package com.intervueai.backend.ai.llm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
@Primary
public class NVIDIAProvider implements LLMProvider {

    private final RestClient restClient;
    private final String model;

    public NVIDIAProvider(
            @Value("${nvidia.api-key}") String apiKey,
            @Value("${nvidia.base-url}") String baseUrl,
            @Value("${nvidia.model}") String model
    ) {

        this.model = model;

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + apiKey
                )
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }

    @Override
    public String generate(String prompt) {

        Map<String, Object> requestBody = Map.of(
                "model", model,

                "messages", List.of(

                        Map.of(
                                "role", "system",
                                "content",
                                """
                                You are an AI interview evaluator.

                                Your task is to evaluate the candidate's answer
                                to the interview question.

                                IMPORTANT:
                                - Return ONLY valid JSON.
                                - Do NOT use Markdown.
                                - Do NOT use code fences.
                                - Do NOT provide explanations outside JSON.
                                - Do NOT truncate the response.
                                - All score values must be integers from 0 to 10.
                                - recommendation must be exactly one of:
                                  STRONG_HIRE, HIRE, CONSIDER, NO_HIRE.
                                - Return exactly these JSON fields:
                                  overallScore
                                  technicalScore
                                  communicationScore
                                  problemSolvingScore
                                  recommendation
                                  strengths
                                  weaknesses
                                  feedback
                                """
                        ),

                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                ),

                "temperature", 0.2,

                /*
                 * Evaluation responses contain detailed feedback.
                 * 150 tokens was too small and caused NVIDIA to
                 * return incomplete JSON.
                 */
                "max_tokens", 800,

                /*
                 * Nemotron reasoning models can consume tokens
                 * for internal thinking. Disable thinking so the
                 * available output budget is used for the JSON.
                 */
                "chat_template_kwargs",
                Map.of(
                        "enable_thinking", false
                )
        );

        Map<?, ?> response = restClient.post()
                .uri("/chat/completions")
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new RuntimeException(
                    "Empty response received from NVIDIA API"
            );
        }

        List<?> choices = (List<?>) response.get("choices");

        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException(
                    "No choices returned from NVIDIA API"
            );
        }

        Map<?, ?> firstChoice =
                (Map<?, ?>) choices.get(0);

        Map<?, ?> message =
                (Map<?, ?>) firstChoice.get("message");

        if (message == null) {
            throw new RuntimeException(
                    "No message returned from NVIDIA API"
            );
        }

        Object contentObject = message.get("content");

        if (contentObject == null) {
            throw new RuntimeException(
                    "No content returned from NVIDIA API"
            );
        }

        String content = contentObject
                .toString()
                .trim();

        if (content.isBlank()) {
            throw new RuntimeException(
                    "NVIDIA returned an empty answer"
            );
        }

        return content;
    }
}
