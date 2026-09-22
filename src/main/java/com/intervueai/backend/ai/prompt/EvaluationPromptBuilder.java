package com.intervueai.backend.ai.prompt;

import com.intervueai.backend.interview.entity.InterviewQuestion;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public String buildBatchPrompt(List<InterviewQuestion> questions) {
        StringBuilder transcript = new StringBuilder();
        int count = 1;
        for (InterviewQuestion q : questions) {
            String answer = q.getCandidateAnswer();
            if (answer == null || answer.isBlank()) {
                answer = "[Candidate did not provide an answer]";
            }
            transcript.append("Q").append(count).append(": ").append(q.getQuestion()).append("\n");
            transcript.append("Candidate Answer: ").append(answer).append("\n\n");
            count++;
        }

        return """
                You are a senior AI technical interview evaluator for a software engineering platform.
                
                Below is the full transcript of the interview session:
                
                %s
                
                Evaluate the candidate's overall performance across all technical questions based on:
                1. Technical depth and accuracy
                2. Communication clarity and articulation
                3. Problem solving approach and logic
                
                IMPORTANT RULES:
                - Return ONLY valid raw JSON object.
                - Do NOT wrap in markdown code blocks like ```json ... ```.
                - Do NOT write any conversational text before or after the JSON.
                - All scores must be integer numbers from 1 to 10.
                - recommendation MUST be exactly one of: "STRONG_HIRE", "HIRE", "CONSIDER", "NO_HIRE".
                - strengths: concise bullet points or paragraph summarizing overall key technical strengths demonstrated.
                - weaknesses: constructive bullet points or paragraph identifying areas for technical improvement.
                - feedback: overall summary feedback for candidate career growth.
                
                Required JSON structure:
                {
                  "overallScore": 8,
                  "technicalScore": 8,
                  "communicationScore": 7,
                  "problemSolvingScore": 8,
                  "recommendation": "HIRE",
                  "strengths": "Key technical strengths observed...",
                  "weaknesses": "Areas needing improvement...",
                  "feedback": "Comprehensive interview summary and advice..."
                }
                """.formatted(transcript.toString());
    }
}
