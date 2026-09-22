package com.intervueai.backend.interview.dto;

public class AIInterviewResponse {

    private Long interviewId;
    private Integer questionNumber;
    private String transitionMessage;
    private String question;
    private String status;
    private boolean completed;

    public AIInterviewResponse() {
    }

    public AIInterviewResponse(
            Long interviewId,
            Integer questionNumber,
            String transitionMessage,
            String question,
            String status,
            boolean completed
    ) {
        this.interviewId = interviewId;
        this.questionNumber = questionNumber;
        this.transitionMessage = transitionMessage;
        this.question = question;
        this.status = status;
        this.completed = completed;
    }

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getTransitionMessage() {
        return transitionMessage;
    }

    public void setTransitionMessage(String transitionMessage) {
        this.transitionMessage = transitionMessage;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
