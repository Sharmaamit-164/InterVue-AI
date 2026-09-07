package com.intervueai.backend.interview.repository;

import com.intervueai.backend.interview.entity.Interview;
import com.intervueai.backend.interview.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterviewQuestionRepository
        extends JpaRepository<InterviewQuestion, Long> {

    List<InterviewQuestion> findByInterviewOrderByQuestionNumberAsc(
            Interview interview
    );

    Optional<InterviewQuestion> findByInterviewAndQuestionNumber(
            Interview interview,
            Integer questionNumber
    );

    long countByInterview(Interview interview);
}