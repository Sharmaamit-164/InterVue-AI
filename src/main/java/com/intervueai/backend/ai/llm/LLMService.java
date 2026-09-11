package com.intervueai.backend.ai.llm;

public interface LLMService {

    /**
     * Sends a prompt to the configured Large Language Model
     * and returns the generated response.
     *
     * @param prompt prompt/instruction sent to the LLM
     * @return generated response from the LLM
     */
    String generateResponse(String prompt);
}