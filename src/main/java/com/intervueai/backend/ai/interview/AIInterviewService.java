package com.intervueai.backend.ai.interview;

public interface AIInterviewService {

    /**
     * Generates the first interview question using
     * the candidate's stored resume and selected job.
     *
     * This method is useful for standalone AI question testing.
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
     * This method is useful for standalone AI question testing.
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

    /**
     * Generates and saves the first question for an interview.
     *
     * The generated question will be stored in the
     * interview_questions table as question number 1.
     *
     * @param email logged-in user's email
     * @param interviewId interview ID
     * @return generated and saved first question
     */
    String generateFirstQuestionForInterview(
            String email,
            Long interviewId
    );

    /**
     * Saves the candidate's answer to the current question
     * and generates the next question.
     *
     * The next question is stored in the interview_questions table.
     *
     * The interview will contain a maximum of 10 questions.
     *
     * @param email logged-in user's email
     * @param interviewId interview ID
     * @param candidateAnswer candidate's answer to the current question
     * @return generated next question
     */
    String generateNextQuestionForInterview(
            String email,
            Long interviewId,
            String candidateAnswer
    );
}