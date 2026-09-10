package com.intervueai.backend.evaluation.service;

import com.intervueai.backend.evaluation.dto.EvaluationResponse;

public interface EvaluationService {

    EvaluationResponse generateEvaluation(
            String email,
            Long interviewId
    );

    EvaluationResponse getEvaluation(
            String email,
            Long interviewId
    );
}