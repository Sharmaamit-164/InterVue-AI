package com.intervueai.backend.interview.dto;

import jakarta.validation.constraints.NotBlank;

public class CandidateAnswerRequest {

    @NotBlank(message = "Candidate answer cannot be empty")
    private String candidateAnswer;

    public CandidateAnswerRequest() {
    }

    public String getCandidateAnswer() {
        return candidateAnswer;
    }

    public void setCandidateAnswer(String candidateAnswer) {
        this.candidateAnswer = candidateAnswer;
    }
}