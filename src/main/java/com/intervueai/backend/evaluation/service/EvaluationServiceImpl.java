package com.intervueai.backend.evaluation.service;

import com.intervueai.backend.ai.evaluation.AIEvaluationService;

import com.intervueai.backend.evaluation.dto.AIEvaluationResponse;
import com.intervueai.backend.evaluation.dto.EvaluationResponse;
import com.intervueai.backend.evaluation.entity.Evaluation;
import com.intervueai.backend.evaluation.repository.EvaluationRepository;
import com.intervueai.backend.interview.entity.Interview;
import com.intervueai.backend.interview.entity.InterviewQuestion;
import com.intervueai.backend.interview.repository.InterviewQuestionRepository;
import com.intervueai.backend.interview.repository.InterviewRepository;
import com.intervueai.backend.user.entity.User;
import com.intervueai.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EvaluationServiceImpl implements EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final UserRepository userRepository;
    private final AIEvaluationService aiEvaluationService;

    public EvaluationServiceImpl(
            EvaluationRepository evaluationRepository,
            InterviewRepository interviewRepository,
            InterviewQuestionRepository interviewQuestionRepository,
            UserRepository userRepository,
            AIEvaluationService aiEvaluationService
    ) {
        this.evaluationRepository = evaluationRepository;
        this.interviewRepository = interviewRepository;
        this.interviewQuestionRepository = interviewQuestionRepository;
        this.userRepository = userRepository;
        this.aiEvaluationService = aiEvaluationService;
    }

    @Override
    @Transactional
    public EvaluationResponse generateEvaluation(
            String email,
            Long interviewId
    ) {

        User user = findUser(email);

        Interview interview = interviewRepository
                .findByIdAndUser(interviewId, user)
                .orElseThrow(() -> new RuntimeException(
                        "Interview not found for the logged-in user"
                ));

        if (!"COMPLETED".equalsIgnoreCase(interview.getStatus())) {
            interview.setStatus("COMPLETED");
            if (interview.getEndedAt() == null) {
                interview.setEndedAt(LocalDateTime.now());
            }
            interviewRepository.save(interview);
        }

        Evaluation existingEvaluation = evaluationRepository
                .findByInterview(interview)
                .orElse(null);

        if (existingEvaluation != null) {
            return convertToResponse(existingEvaluation);
        }

        List<InterviewQuestion> questions =
                interviewQuestionRepository
                        .findByInterviewOrderByQuestionNumberAsc(interview);

        if (questions.isEmpty()) {
            Evaluation fallback = new Evaluation();
            fallback.setInterview(interview);
            fallback.setOverallScore(6);
            fallback.setTechnicalScore(6);
            fallback.setCommunicationScore(6);
            fallback.setProblemSolvingScore(6);
            fallback.setRecommendation("CONSIDER");
            fallback.setStrengths("Interview session initiated successfully.");
            fallback.setWeaknesses("No questions were recorded in this interview session.");
            fallback.setFeedback("Please attempt a new interview session and answer the technical questions.");
            fallback.setUpdatedAt(LocalDateTime.now());
            Evaluation saved = evaluationRepository.save(fallback);
            return convertToResponse(saved);
        }

        AIEvaluationResponse aiEval = aiEvaluationService.evaluateBatch(questions);

        Evaluation evaluation = new Evaluation();
        evaluation.setInterview(interview);
        evaluation.setOverallScore(aiEval.getOverallScore());
        evaluation.setTechnicalScore(aiEval.getTechnicalScore());
        evaluation.setCommunicationScore(aiEval.getCommunicationScore());
        evaluation.setProblemSolvingScore(aiEval.getProblemSolvingScore());
        evaluation.setRecommendation(aiEval.getRecommendation());
        evaluation.setStrengths(aiEval.getStrengths());
        evaluation.setWeaknesses(aiEval.getWeaknesses());
        evaluation.setFeedback(aiEval.getFeedback());
        evaluation.setUpdatedAt(LocalDateTime.now());

        Evaluation savedEvaluation = evaluationRepository.save(evaluation);
        return convertToResponse(savedEvaluation);
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluationResponse getEvaluation(
            String email,
            Long interviewId
    ) {

        User user = findUser(email);

        Interview interview = interviewRepository
                .findByIdAndUser(interviewId, user)
                .orElseThrow(() -> new RuntimeException(
                        "Interview not found for the logged-in user"
                ));

        Evaluation evaluation = evaluationRepository
                .findByInterview(interview)
                .orElseThrow(() -> new RuntimeException(
                        "Evaluation not found for this interview"
                ));

        return convertToResponse(evaluation);
    }

    private User findUser(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "User not found"
                ));
    }

    private int calculateAverage(
            int total,
            int count
    ) {

        return (int) Math.round(
                (double) total / count
        );
    }

    private String generateRecommendation(
            int overallScore
    ) {

        if (overallScore >= 8) {
            return "STRONG_HIRE";
        }

        if (overallScore >= 6) {
            return "HIRE";
        }

        if (overallScore >= 4) {
            return "CONSIDER";
        }

        return "NO_HIRE";
    }

    private EvaluationResponse convertToResponse(
            Evaluation evaluation
    ) {

        return new EvaluationResponse(
                evaluation.getId(),
                evaluation.getInterview().getId(),
                evaluation.getOverallScore(),
                evaluation.getTechnicalScore(),
                evaluation.getCommunicationScore(),
                evaluation.getProblemSolvingScore(),
                evaluation.getRecommendation(),
                evaluation.getStrengths(),
                evaluation.getWeaknesses(),
                evaluation.getFeedback(),
                evaluation.getCreatedAt(),
                evaluation.getUpdatedAt()
        );
    }
}

