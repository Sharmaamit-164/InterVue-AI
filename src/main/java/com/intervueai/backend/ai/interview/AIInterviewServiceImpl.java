package com.intervueai.backend.ai.interview;

import com.intervueai.backend.ai.llm.LLMService;
import com.intervueai.backend.interview.entity.Interview;
import com.intervueai.backend.interview.entity.InterviewQuestion;
import com.intervueai.backend.interview.repository.InterviewQuestionRepository;
import com.intervueai.backend.interview.repository.InterviewRepository;
import com.intervueai.backend.job.entity.Job;
import com.intervueai.backend.resume.entity.Resume;
import com.intervueai.backend.user.entity.User;
import com.intervueai.backend.job.repository.JobRepository;
import com.intervueai.backend.resume.repository.ResumeRepository;
import com.intervueai.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AIInterviewServiceImpl implements AIInterviewService {

    private static final int MAX_QUESTIONS = 10;

    private final LLMService llmService;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;

    public AIInterviewServiceImpl(
            LLMService llmService,
            UserRepository userRepository,
            ResumeRepository resumeRepository,
            JobRepository jobRepository,
            InterviewRepository interviewRepository,
            InterviewQuestionRepository interviewQuestionRepository
    ) {
        this.llmService = llmService;
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.interviewRepository = interviewRepository;
        this.interviewQuestionRepository = interviewQuestionRepository;
    }

    /*
     * ============================================================
     * EXISTING STANDALONE AI METHODS
     * ============================================================
     */

    @Override
    public String generateFirstQuestion(
            String email,
            Long resumeId,
            Long jobId
    ) {

        User user = findUser(email);

        Resume resume = resumeRepository
                .findByIdAndUser(resumeId, user)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Resume not found for the logged-in user"
                        )
                );

        Job job = jobRepository
                .findByIdAndUser(jobId, user)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Job not found for the logged-in user"
                        )
                );

        String resumeText = resume.getParsedText();

        if (resumeText == null || resumeText.isBlank()) {
            throw new IllegalArgumentException(
                    "The selected resume does not contain parsed text"
            );
        }

        String prompt = buildFirstQuestionPrompt(
                resumeText,
                job
        );

        return llmService.generateResponse(prompt);
    }

    @Override
    public String generateNextQuestion(
            String email,
            Long resumeId,
            Long jobId,
            String previousConversation
    ) {

        User user = findUser(email);

        Resume resume = resumeRepository
                .findByIdAndUser(resumeId, user)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Resume not found for the logged-in user"
                        )
                );

        Job job = jobRepository
                .findByIdAndUser(jobId, user)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Job not found for the logged-in user"
                        )
                );

        String resumeText = resume.getParsedText();

        if (resumeText == null || resumeText.isBlank()) {
            throw new IllegalArgumentException(
                    "The selected resume does not contain parsed text"
            );
        }

        String conversation = previousConversation == null
                ? ""
                : previousConversation.trim();

        String prompt = buildNextQuestionPrompt(
                resumeText,
                job,
                conversation
        );

        return llmService.generateResponse(prompt);
    }

    /*
     * ============================================================
     * ACTUAL INTERVIEW FLOW
     * ============================================================
     */

    @Override
    @Transactional
    public String generateFirstQuestionForInterview(
            String email,
            Long interviewId
    ) {

        User user = findUser(email);

        Interview interview = interviewRepository
                .findByIdAndUser(interviewId, user)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Interview not found for the logged-in user"
                        )
                );

        if ("COMPLETED".equalsIgnoreCase(interview.getStatus())) {
            throw new IllegalStateException(
                    "This interview has already been completed"
            );
        }

        long existingQuestionCount =
                interviewQuestionRepository.countByInterview(interview);

        if (existingQuestionCount > 0) {
            throw new IllegalStateException(
                    "The first question has already been generated"
            );
        }

        Resume resume = interview.getResume();
        Job job = interview.getJob();

        String resumeText = resume.getParsedText();

        if (resumeText == null || resumeText.isBlank()) {
            throw new IllegalArgumentException(
                    "The selected resume does not contain parsed text"
            );
        }

        String prompt = buildFirstQuestionPrompt(
                resumeText,
                job
        );

        String generatedQuestion =
                llmService.generateResponse(prompt);

        if (generatedQuestion == null ||
                generatedQuestion.isBlank()) {

            throw new RuntimeException(
                    "AI failed to generate the first interview question"
            );
        }

        InterviewQuestion question =
                new InterviewQuestion();

        question.setInterview(interview);
        question.setQuestionNumber(1);
        question.setQuestion(generatedQuestion.trim());
        question.setAskedAt(LocalDateTime.now());

        interviewQuestionRepository.save(question);

        return question.getQuestion();
    }

    @Override
    @Transactional
    public String generateNextQuestionForInterview(
            String email,
            Long interviewId,
            String candidateAnswer
    ) {

        User user = findUser(email);

        Interview interview = interviewRepository
                .findByIdAndUser(interviewId, user)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Interview not found for the logged-in user"
                        )
                );

        if ("COMPLETED".equalsIgnoreCase(interview.getStatus())) {
            throw new IllegalStateException(
                    "This interview has already been completed"
            );
        }

        if (candidateAnswer == null ||
                candidateAnswer.trim().isBlank()) {

            throw new IllegalArgumentException(
                    "Candidate answer cannot be empty"
            );
        }

        List<InterviewQuestion> questions =
                interviewQuestionRepository
                        .findByInterviewOrderByQuestionNumberAsc(
                                interview
                        );

        if (questions.isEmpty()) {
            throw new IllegalStateException(
                    "No interview question exists. Generate the first question first."
            );
        }

        InterviewQuestion currentQuestion =
                questions.get(questions.size() - 1);

        /*
         * Make sure the previous question has not already
         * been answered.
         */
        if (currentQuestion.getCandidateAnswer() != null &&
                !currentQuestion.getCandidateAnswer().isBlank()) {

            throw new IllegalStateException(
                    "The current question has already been answered"
            );
        }

        /*
         * Save candidate answer.
         */
        currentQuestion.setCandidateAnswer(
                candidateAnswer.trim()
        );

        currentQuestion.setAnsweredAt(
                LocalDateTime.now()
        );

        interviewQuestionRepository.save(currentQuestion);

        /*
         * If this was question 10, the interview is complete.
         * DO NOT generate question 11.
         */
        if (currentQuestion.getQuestionNumber() >= MAX_QUESTIONS) {

            interview.setStatus("COMPLETED");

            interview.setEndedAt(
                    LocalDateTime.now()
            );

            interviewRepository.save(interview);

            return "INTERVIEW_COMPLETED";
        }

        /*
         * Build conversation from all stored questions
         * and answers.
         */
        String conversation =
                buildConversation(questions);

        Resume resume = interview.getResume();
        Job job = interview.getJob();

        String resumeText = resume.getParsedText();

        if (resumeText == null || resumeText.isBlank()) {
            throw new IllegalArgumentException(
                    "The selected resume does not contain parsed text"
            );
        }

        String prompt = buildNextQuestionPrompt(
                resumeText,
                job,
                conversation
        );

        String generatedQuestion =
                llmService.generateResponse(prompt);

        if (generatedQuestion == null ||
                generatedQuestion.isBlank()) {

            throw new RuntimeException(
                    "AI failed to generate the next interview question"
            );
        }

        int nextQuestionNumber =
                currentQuestion.getQuestionNumber() + 1;

        InterviewQuestion nextQuestion =
                new InterviewQuestion();

        nextQuestion.setInterview(interview);
        nextQuestion.setQuestionNumber(
                nextQuestionNumber
        );
        nextQuestion.setQuestion(
                generatedQuestion.trim()
        );
        nextQuestion.setAskedAt(
                LocalDateTime.now()
        );

        interviewQuestionRepository.save(nextQuestion);

        return nextQuestion.getQuestion();
    }

    /*
     * ============================================================
     * USER
     * ============================================================
     */

    private User findUser(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );
    }

    /*
     * ============================================================
     * CONVERSATION BUILDER
     * ============================================================
     */

    private String buildConversation(
            List<InterviewQuestion> questions
    ) {

        StringBuilder conversation =
                new StringBuilder();

        for (InterviewQuestion question : questions) {

            conversation
                    .append("Question ")
                    .append(question.getQuestionNumber())
                    .append(": ")
                    .append(question.getQuestion())
                    .append("\n");

            if (question.getCandidateAnswer() != null &&
                    !question.getCandidateAnswer().isBlank()) {

                conversation
                        .append("Candidate Answer: ")
                        .append(question.getCandidateAnswer())
                        .append("\n");
            }

            conversation.append("\n");
        }

        return conversation.toString().trim();
    }

    /*
     * ============================================================
     * FIRST QUESTION PROMPT
     * ============================================================
     */

    private String buildFirstQuestionPrompt(
            String resumeText,
            Job job
    ) {

        return """
                TASK: Generate the first question of a technical job interview.

                OUTPUT REQUIREMENT:
                Return ONLY ONE interview question.
                Start directly with the question.
                End with "?".

                DO NOT:
                - provide reasoning
                - provide analysis
                - provide a thinking process
                - provide an answer
                - provide explanations
                - provide headings
                - provide bullet points
                - provide numbering
                - ask more than one question
                - mention AI

                Candidate Resume:
                %s

                Target Job:
                %s

                Company:
                %s

                Job Description:
                %s

                Required Skills:
                %s

                Ask one relevant question based on the candidate's resume
                and the target job.
                """.formatted(
                resumeText,
                job.getTitle(),
                job.getCompanyName(),
                job.getDescription(),
                job.getRequiredSkills()
        );
    }

    /*
     * ============================================================
     * NEXT QUESTION PROMPT
     * ============================================================
     */

    private String buildNextQuestionPrompt(
            String resumeText,
            Job job,
            String previousConversation
    ) {

        return """
                TASK: Continue a technical job interview.

                OUTPUT REQUIREMENT:
                Return ONLY ONE interview question.
                Start directly with the question.
                End with "?".

                DO NOT:
                - provide reasoning
                - provide analysis
                - provide a thinking process
                - provide an answer
                - provide explanations
                - provide headings
                - provide bullet points
                - provide numbering
                - ask more than one question
                - repeat a previous question
                - mention AI

                Candidate Resume:
                %s

                Target Job:
                %s

                Company:
                %s

                Job Description:
                %s

                Required Skills:
                %s

                Previous Conversation:
                %s

                Based on the resume, job and previous conversation,
                ask the single best next interview question.
                """.formatted(
                resumeText,
                job.getTitle(),
                job.getCompanyName(),
                job.getDescription(),
                job.getRequiredSkills(),
                previousConversation
        );
    }
}