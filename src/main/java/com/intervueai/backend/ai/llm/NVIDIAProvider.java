package com.intervueai.backend.ai.llm;

import com.intervueai.backend.ai.llm.LLMProvider;
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
                                You are a professional technical interviewer.

                                Return ONLY ONE interview question.

                                Do not provide reasoning.
                                Do not provide analysis.
                                Do not provide a thinking process.
                                Do not provide an answer.
                                Do not provide explanations.
                                Do not use headings.
                                Do not use bullet points.
                                Do not use numbering.
                                Do not ask multiple questions.

                                Start directly with the interview question.
                                """
                        ),

                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                ),

                "temperature", 0.2,

                "max_tokens", 150,

                /*
                 * IMPORTANT:
                 * Nemotron 3.5 Lightning is a reasoning model.
                 * Disable thinking so the complete token budget
                 * is used for the actual interview question.
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

        return cleanQuestion(content);
    }

    private String cleanQuestion(String content) {

        /*
         * Remove accidental markdown formatting.
         */
        content = content
                .replace("```", "")
                .trim();

        /*
         * Remove common numbering.
         */
        content = content
                .replaceFirst("^\\d+[.)]\\s*", "")
                .trim();

        /*
         * Remove bullet points.
         */
        content = content
                .replaceFirst("^[*-]\\s*", "")
                .trim();

        /*
         * If the model somehow returned additional text
         * before the actual question, extract the question.
         */
        int questionMark = content.indexOf('?');

        if (questionMark >= 0) {

            String question = content
                    .substring(0, questionMark + 1)
                    .trim();

            /*
             * If there is obvious prefix text, remove it.
             */
            int lastNewLine = question.lastIndexOf("\n");

            if (lastNewLine >= 0) {
                String lastLine = question
                        .substring(lastNewLine + 1)
                        .trim();

                if (!lastLine.isBlank()) {
                    question = lastLine;
                }
            }

            return question;
        }

        return content;
    }
}

