package com.intervueai.backend.ai.evaluation;


import com.intervueai.backend.evaluation.dto.AIEvaluationResponse;

public interface AIEvaluationService {

    AIEvaluationResponse evaluate(
            String question,
            String answer
    );
}

