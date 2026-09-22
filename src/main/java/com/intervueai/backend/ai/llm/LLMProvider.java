package com.intervueai.backend.ai.llm;

public interface LLMProvider {

    /**
     * Sends a prompt to the configured language model
     * and returns the generated text response.
     *
     * @param prompt prompt/instructions sent to the LLM
     * @return generated response from the LLM
     */
    String generate(String prompt);

    /**
     * Sends a system prompt and user prompt to the configured language model
     * and returns the generated text response.
     *
     * @param systemPrompt system instructions for the LLM
     * @param userPrompt   user input / context sent to the LLM
     * @return generated response from the LLM
     */
    default String generate(String systemPrompt, String userPrompt) {
        if (systemPrompt == null || systemPrompt.isBlank()) {
            return generate(userPrompt);
        }
        return generate(systemPrompt + "\n\n" + userPrompt);
    }
}