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

        int evaluatedQuestions = 0;

        int totalOverallScore = 0;
        int totalTechnicalScore = 0;
        int totalCommunicationScore = 0;
        int totalProblemSolvingScore = 0;

        StringBuilder strengths = new StringBuilder();
        StringBuilder weaknesses = new StringBuilder();
        StringBuilder feedback = new StringBuilder();

        for (InterviewQuestion question : questions) {

            String candidateAnswer = question.getCandidateAnswer();

            if (candidateAnswer == null || candidateAnswer.isBlank()) {
                continue;
            }

            AIEvaluationResponse aiEvaluation =
                    aiEvaluationService.evaluate(
                            question.getQuestion(),
                            candidateAnswer
                    );

            evaluatedQuestions++;

            totalOverallScore += aiEvaluation.getOverallScore();
            totalTechnicalScore += aiEvaluation.getTechnicalScore();
            totalCommunicationScore += aiEvaluation.getCommunicationScore();
            totalProblemSolvingScore +=
                    aiEvaluation.getProblemSolvingScore();

            strengths.append("Question ")
                    .append(question.getQuestionNumber())
                    .append(": ")
                    .append(aiEvaluation.getStrengths())
                    .append("\n\n");

            weaknesses.append("Question ")
                    .append(question.getQuestionNumber())
                    .append(": ")
                    .append(aiEvaluation.getWeaknesses())
                    .append("\n\n");

            feedback.append("Question ")
                    .append(question.getQuestionNumber())
                    .append(":\n")
                    .append(aiEvaluation.getFeedback())
                    .append("\n\n");
        }

        if (evaluatedQuestions == 0) {
            throw new RuntimeException(
                    "No answered questions found for AI evaluation"
            );
        }

        int overallScore = calculateAverage(
                totalOverallScore,
                evaluatedQuestions
        );

        int technicalScore = calculateAverage(
                totalTechnicalScore,
                evaluatedQuestions
        );

        int communicationScore = calculateAverage(
                totalCommunicationScore,
                evaluatedQuestions
        );

        int problemSolvingScore = calculateAverage(
                totalProblemSolvingScore,
                evaluatedQuestions
        );

        String recommendation =
                generateRecommendation(overallScore);

        Evaluation evaluation = new Evaluation();

        evaluation.setInterview(interview);

        evaluation.setOverallScore(overallScore);
        evaluation.setTechnicalScore(technicalScore);
        evaluation.setCommunicationScore(communicationScore);
        evaluation.setProblemSolvingScore(problemSolvingScore);

        evaluation.setRecommendation(recommendation);

        evaluation.setStrengths(
                strengths.toString().trim()
        );

        evaluation.setWeaknesses(
                weaknesses.toString().trim()
        );

        evaluation.setFeedback(
                feedback.toString().trim()
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

