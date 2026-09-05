package com.intervueai.backend.ai.interview;

public interface AIInterviewService {

    /**
     * Generates the first interview question using
     * the candidate's stored resume and selected job.
     *
     * @param email logged-in user's email
     * @param resumeId stored resume ID
     * @param jobId selected job ID
     * @return dynamically generated interview question
     */
    String generateFirstQuestion(
            String email,
            Long resumeId,
            Long jobId
    );

    /**
     * Generates the next interview question using the
     * candidate's resume, selected job, and previous
     * interview conversation.
     *
     * @param email logged-in user's email
     * @param resumeId stored resume ID
     * @param jobId selected job ID
     * @param previousConversation previous questions and answers
     * @return dynamically generated next interview question
     */
    String generateNextQuestion(
            String email,
            Long resumeId,
            Long jobId,
            String previousConversation
    );
}