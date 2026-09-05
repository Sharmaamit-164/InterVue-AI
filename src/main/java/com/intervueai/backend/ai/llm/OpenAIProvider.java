package com.intervueai.backend.ai.llm;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import org.springframework.stereotype.Component;

@Component
public class OpenAIProvider implements LLMProvider {

    private final OpenAIClient openAIClient;

    public OpenAIProvider(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    @Override
    public String generate(String prompt) {

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt cannot be empty");
        }

        ResponseCreateParams params = ResponseCreateParams.builder()
                .model(ChatModel.GPT_5_2)
                .input(prompt)
                .build();

        Response response = openAIClient.responses().create(params);

        return response.output().stream()
                .flatMap(outputItem -> outputItem.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .map(outputText -> outputText.text())
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("OpenAI returned an empty response"));
    }
}