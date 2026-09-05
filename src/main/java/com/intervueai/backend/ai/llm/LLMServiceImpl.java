package com.intervueai.backend.ai.llm;

import org.springframework.stereotype.Service;

@Service
public class LLMServiceImpl implements LLMService {

    private final LLMProvider llmProvider;

    public LLMServiceImpl(LLMProvider llmProvider) {
        this.llmProvider = llmProvider;
    }

    @Override
    public String generateResponse(String prompt) {

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt cannot be empty");
        }

        return llmProvider.generate(prompt);
    }
}