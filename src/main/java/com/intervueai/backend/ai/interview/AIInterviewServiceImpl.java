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

        /*
         * ========================================================
         * FIRST INTERVIEW QUESTION
         * ========================================================
         *
         * The interview always starts with a self-introduction.
         * We do NOT ask NVIDIA to generate this question.
         */

        String generatedQuestion =
                "Tell me about yourself.";

        InterviewQuestion question =
                new InterviewQuestion();

        question.setInterview(interview);
        question.setQuestionNumber(1);

        question.setQuestion(
                generatedQuestion
        );

        question.setAskedAt(
                LocalDateTime.now()
        );

        interviewQuestionRepository.save(question);

        return question.getQuestion();
    }

    /*
     * ============================================================
     * NEXT INTERVIEW QUESTION
     * ============================================================
     */

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

        /*
         * Get all questions belonging to this interview.
         */
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

        /*
         * The latest question is the question
         * that the candidate is answering now.
         */
        InterviewQuestion currentQuestion =
                questions.get(questions.size() - 1);

        /*
         * Prevent answering the same question twice.
         */
        if (currentQuestion.getCandidateAnswer() != null &&
                !currentQuestion.getCandidateAnswer().isBlank()) {

            throw new IllegalStateException(
                    "The current question has already been answered"
            );
        }

        /*
         * ========================================================
         * SAVE CANDIDATE ANSWER
         * ========================================================
         */

        currentQuestion.setCandidateAnswer(
                candidateAnswer.trim()
        );

        currentQuestion.setAnsweredAt(
                LocalDateTime.now()
        );

        interviewQuestionRepository.save(currentQuestion);

        /*
         * ========================================================
         * INTRODUCTION COMPLETED
         * ========================================================
         *
         * Question 1 is:
         *
         * "Tell me about yourself."
         *
         * After the candidate answers it, we don't immediately
         * ask another random question.
         *
         * We first give a natural interviewer transition.
         */

        if (currentQuestion.getQuestionNumber() == 1) {

            Resume resume = interview.getResume();
            Job job = interview.getJob();

            String resumeText = resume.getParsedText();

            if (resumeText == null || resumeText.isBlank()) {
                throw new IllegalArgumentException(
                        "The selected resume does not contain parsed text"
                );
            }

            String conversation =
                    buildConversation(questions);

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
                        "AI failed to generate the first technical interview question"
                );
            }

            int nextQuestionNumber = 2;

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

            interviewQuestionRepository.save(
                    nextQuestion
            );

            return
                    "Okay, thank you. Now I will ask you some technical questions."
                            + "\n\n"
                            + generatedQuestion.trim();
        }

        /*
         * ========================================================
         * TECHNICAL QUESTIONS
         * ========================================================
         *
         * Question 2 onwards follows the normal technical
         * interview flow.
         */

        if (currentQuestion.getQuestionNumber() >= MAX_QUESTIONS) {

            interview.setStatus("COMPLETED");

            interview.setEndedAt(
                    LocalDateTime.now()
            );

            interviewRepository.save(interview);

            return "Thank you. That completes your interview.";
        }

        /*
         * Build the complete conversation so the LLM can
         * understand what has already been discussed.
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

        interviewQuestionRepository.save(
                nextQuestion
        );

        /*
         * ========================================================
         * NATURAL INTERVIEW TRANSITION
         * ========================================================
         */

        String transitionMessage =
                getTransitionMessage(
                        currentQuestion.getQuestionNumber()
                );

        return transitionMessage
                + "\n\n"
                + generatedQuestion.trim();
    }

    /*
     * ============================================================
     * INTERVIEWER TRANSITION MESSAGES
     * ============================================================
     */

    private String getTransitionMessage(
            int questionNumber
    ) {

        return switch (questionNumber) {

            case 2 ->
                    "Hmm, okay. Let's move to the next question.";

            case 3 ->
                    "Alright, thank you. Let's continue with the next question.";

            case 4 ->
                    "Okay, good. Let's move on to the next question.";

            case 5 ->
                    "Alright. Let's explore another technical topic.";

            case 6 ->
                    "Hmm, okay. Let's continue.";

            case 7 ->
                    "Thank you. Let's move to the next question.";

            case 8 ->
                    "Alright, let's keep going with another question.";

            case 9 ->
                    "Okay, one more technical question.";

            default ->
                    "Alright, let's move to the next question.";
        };
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
     *
     * This is kept for the standalone AI endpoint.
     * The actual interview flow now uses the fixed
     * "Tell me about yourself." question.
     */

    private String buildFirstQuestionPrompt(
            String resumeText,
            Job job
    ) {

        return """
                TASK: Generate the first question of a technical job interview.

                STRICT QUESTION FORMAT:
                - Return ONLY ONE question.
                - Keep the question SHORT and DIRECT.
                - Prefer 5 to 12 words.
                - Never exceed 15 words.
                - Use ONE sentence only.
                - Ask about ONE concept only.
                - Use simple conversational interview language.
                - Start directly with the question.
                - End with "?".

                GOOD EXAMPLES:
                What is Spring Boot?
                What is dependency injection?
                What is JPA?
                What is optimistic locking?
                How does JWT authentication work?
                What is the difference between JPA and JDBC?

                AVOID:
                - long questions
                - multi-part questions
                - multiple questions
                - long scenarios
                - hypothetical situations
                - explanations
                - reasoning
                - analysis
                - headings
                - bullet points
                - numbering
                - answers
                - mentioning AI

                The question should be relevant to the candidate's
                resume and the target job.

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

                Generate one short interview question now.
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

                STRICT QUESTION FORMAT:
                - Return ONLY ONE question.
                - Keep the question SHORT and DIRECT.
                - Prefer 5 to 12 words.
                - Never exceed 15 words.
                - Use ONE sentence only.
                - Ask about ONE concept only.
                - Use simple conversational interview language.
                - Start directly with the question.
                - End with "?".

                GOOD EXAMPLES:
                What is Spring Boot?
                What is dependency injection?
                What is JPA?
                What is optimistic locking?
                How does JWT authentication work?
                What is the difference between JPA and JDBC?

                AVOID:
                - long questions
                - multi-part questions
                - multiple questions
                - long scenarios
                - hypothetical situations
                - explanations
                - reasoning
                - analysis
                - headings
                - bullet points
                - numbering
                - answers
                - mentioning AI
                - repeating a previous question

                The next question should be relevant to the
                resume, target job, and previous conversation.

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

                Generate one short next interview question now.
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
