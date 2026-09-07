package com.intervueai.backend.interview.dto;

import java.time.LocalDateTime;

public class InterviewQuestionResponse {

    private Long id;

    private Long interviewId;

    private Integer questionNumber;

    private String question;

    private String candidateAnswer;

    private LocalDateTime askedAt;

    private LocalDateTime answeredAt;

    public InterviewQuestionResponse() {
    }

    public InterviewQuestionResponse(
            Long id,
            Long interviewId,
            Integer questionNumber,
            String question,
            String candidateAnswer,
            LocalDateTime askedAt,
            LocalDateTime answeredAt
    ) {
        this.id = id;
        this.interviewId = interviewId;
        this.questionNumber = questionNumber;
        this.question = question;
        this.candidateAnswer = candidateAnswer;
        this.askedAt = askedAt;
        this.answeredAt = answeredAt;
    }

    public Long getId() {
        return id;
    }

    public Long getInterviewId() {
        return interviewId;
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public String getQuestion() {
        return question;
    }

    public String getCandidateAnswer() {
        return candidateAnswer;
    }

    public LocalDateTime getAskedAt() {
        return askedAt;
    }

    public LocalDateTime getAnsweredAt() {
        return answeredAt;
    }
}