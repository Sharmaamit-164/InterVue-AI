package com.intervueai.backend.evaluation.service;

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

    public EvaluationServiceImpl(
            EvaluationRepository evaluationRepository,
            InterviewRepository interviewRepository,
            InterviewQuestionRepository interviewQuestionRepository,
            UserRepository userRepository
    ) {
        this.evaluationRepository = evaluationRepository;
        this.interviewRepository = interviewRepository;
        this.interviewQuestionRepository = interviewQuestionRepository;
        this.userRepository = userRepository;
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
            throw new RuntimeException(
                    "Evaluation can only be generated for a completed interview"
            );
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
            throw new RuntimeException(
                    "No interview questions found for evaluation"
            );
        }

        /*
         * Initial evaluation logic.
         *
         * The actual AI evaluation will be connected later.
         * For now, we calculate a basic score from answered questions.
         */

        long answeredQuestions = questions.stream()
                .filter(question ->
                        question.getCandidateAnswer() != null
                                && !question.getCandidateAnswer().isBlank()
                )
                .count();

        int overallScore = (int) Math.round(
                (answeredQuestions * 100.0) / questions.size()
        );

        Evaluation evaluation = new Evaluation();

        evaluation.setInterview(interview);
        evaluation.setOverallScore(overallScore);
        evaluation.setTechnicalScore(overallScore);
        evaluation.setCommunicationScore(overallScore);
        evaluation.setProblemSolvingScore(overallScore);

        if (overallScore >= 80) {
            evaluation.setRecommendation("STRONG_HIRE");
        } else if (overallScore >= 65) {
            evaluation.setRecommendation("HIRE");
        } else if (overallScore >= 50) {
            evaluation.setRecommendation("CONSIDER");
        } else {
            evaluation.setRecommendation("NO_HIRE");
        }

        evaluation.setStrengths(
                "Candidate answered "
                        + answeredQuestions
                        + " out of "
                        + questions.size()
                        + " questions."
        );

        evaluation.setWeaknesses(
                "Detailed AI-based weakness analysis will be added."
        );

        evaluation.setFeedback(
                "Initial interview evaluation generated successfully. "
                        + "AI-based detailed evaluation will be connected next."
        );

        evaluation.setUpdatedAt(LocalDateTime.now());

        Evaluation savedEvaluation =
                evaluationRepository.save(evaluation);

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