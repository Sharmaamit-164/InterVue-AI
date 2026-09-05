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
}