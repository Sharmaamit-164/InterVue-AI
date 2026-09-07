package com.intervueai.backend.interview.service;

import com.intervueai.backend.interview.dto.CreateInterviewRequest;
import com.intervueai.backend.interview.dto.InterviewQuestionResponse;
import com.intervueai.backend.interview.dto.InterviewResponse;

import java.util.List;

public interface InterviewService {

    /**
     * Creates a new interview session for the logged-in user.
     *
     * The interview starts with status IN_PROGRESS.
     *
     * @param email logged-in user's email
     * @param request contains selected resume and job
     * @return created interview session
     */
    InterviewResponse createInterview(
            String email,
            CreateInterviewRequest request
    );

    /**
     * Returns all interview sessions belonging to the logged-in user.
     *
     * @param email logged-in user's email
     * @return list of interviews
     */
    List<InterviewResponse> getMyInterviews(
            String email
    );

    /**
     * Returns one interview session belonging to the logged-in user.
     *
     * @param email logged-in user's email
     * @param interviewId interview ID
     * @return interview details
     */
    InterviewResponse getMyInterview(
            String email,
            Long interviewId
    );

    /**
     * Returns all questions of an interview.
     *
     * Questions are returned in question-number order.
     *
     * @param email logged-in user's email
     * @param interviewId interview ID
     * @return list of interview questions
     */
    List<InterviewQuestionResponse> getInterviewQuestions(
            String email,
            Long interviewId
    );

    /**
     * Marks an interview as completed.
     *
     * @param email logged-in user's email
     * @param interviewId interview ID
     * @return updated interview
     */
    InterviewResponse completeInterview(
            String email,
            Long interviewId
    );
}