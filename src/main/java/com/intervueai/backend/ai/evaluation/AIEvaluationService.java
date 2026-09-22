package com.intervueai.backend.ai.evaluation;

import com.intervueai.backend.evaluation.dto.AIEvaluationResponse;
import com.intervueai.backend.interview.entity.InterviewQuestion;

import java.util.List;

public interface AIEvaluationService {

    AIEvaluationResponse evaluate(
            String question,
            String answer
    );

    AIEvaluationResponse evaluateBatch(
            List<InterviewQuestion> questions
    );
}
