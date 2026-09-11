package com.intervueai.backend.ai.prompt;

import org.springframework.stereotype.Component;

@Component
public class EvaluationPromptBuilder {

    public String buildPrompt(String question, String answer) {

        return """
                You are an AI interview evaluator for an interview practice platform.

                Evaluate the candidate's answer to the interview question.

                Interview Question:
                %s

                Candidate Answer:
                %s

                Evaluate the candidate based on:
                1. Correctness
                2. Technical knowledge
                3. Communication
                4. Problem-solving ability
                5. Relevance and clarity

                IMPORTANT:
                - Return ONLY valid JSON.
                - Do NOT use Markdown.
                - Do NOT use code fences.
                - Do NOT include any explanation outside the JSON.
                - All score values must be integers between 0 and 10.
                - recommendation must be exactly one of:
                  STRONG_HIRE, HIRE, CONSIDER, NO_HIRE
                - Use clear and professional language.
                - Do not add any extra JSON fields.

                Return JSON in exactly this structure:

                {
                  "overallScore": 0,
                  "technicalScore": 0,
                  "communicationScore": 0,
                  "problemSolvingScore": 0,
                  "recommendation": "CONSIDER",
                  "strengths": "Candidate strengths",
                  "weaknesses": "Candidate weaknesses",
                  "feedback": "Detailed feedback and suggestions for improvement"
                }

                """.formatted(question, answer);
    }
}

