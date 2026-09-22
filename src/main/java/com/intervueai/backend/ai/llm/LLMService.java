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

    /**
     * Sends a system prompt and user prompt to the configured Large Language Model
     * and returns the generated response.
     *
     * @param systemPrompt system instructions for the LLM
     * @param userPrompt   user input / context sent to the LLM
     * @return generated response from the LLM
     */
    default String generateResponse(String systemPrompt, String userPrompt) {
        if (systemPrompt == null || systemPrompt.isBlank()) {
            return generateResponse(userPrompt);
        }
        return generateResponse(systemPrompt + "\n\n" + userPrompt);
    }
}