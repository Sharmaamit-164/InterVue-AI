package com.intervueai.backend.ai.llm;

public interface LLMService {

    /**
     * Sends a prompt to the configured LLM and returns the generated response.
     *
     * @param prompt the prompt to send to the language model
     * @return generated text
     */
    String generateResponse(String prompt);
}