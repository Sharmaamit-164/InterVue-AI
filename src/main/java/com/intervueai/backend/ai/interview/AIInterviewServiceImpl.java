package com.intervueai.backend.ai.interview;

import com.intervueai.backend.ai.llm.LLMService;
import com.intervueai.backend.interview.dto.AIInterviewResponse;
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
    public AIInterviewResponse generateFirstQuestionForInterview(
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

        List<InterviewQuestion> existingQuestions =
                interviewQuestionRepository.findByInterviewOrderByQuestionNumberAsc(interview);

        if (!existingQuestions.isEmpty()) {
            InterviewQuestion existingQ1 = existingQuestions.get(0);
            return new AIInterviewResponse(
                    interview.getId(),
                    existingQ1.getQuestionNumber(),
                    "Hello! Welcome to your technical interview. Let's begin with a quick introduction.",
                    existingQ1.getQuestion(),
                    interview.getStatus(),
                    false
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

        interview.setStatus("IN_PROGRESS");
        interviewRepository.save(interview);

        return new AIInterviewResponse(
                interview.getId(),
                1,
                "Hello! Welcome to your technical interview. Let's begin with a quick introduction.",
                question.getQuestion(),
                interview.getStatus(),
                false
        );
    }

    /*
     * ============================================================
     * NEXT INTERVIEW QUESTION
     * ============================================================
     */

    @Override
    @Transactional
    public AIInterviewResponse generateNextQuestionForInterview(
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
        /*
         * Find the current question to answer.
         * Prefer the earliest unanswered question, or the latest question.
         */
        InterviewQuestion currentQuestion = null;
        for (InterviewQuestion q : questions) {
            if (q.getCandidateAnswer() == null || q.getCandidateAnswer().isBlank()) {
                currentQuestion = q;
                break;
            }
        }
        if (currentQuestion == null) {
            currentQuestion = questions.get(questions.size() - 1);
        }

        /*
         * ========================================================
         * SAVE CANDIDATE ANSWER
         * ========================================================
         */
        currentQuestion.setCandidateAnswer(candidateAnswer.trim());
        currentQuestion.setAnsweredAt(LocalDateTime.now());
        interviewQuestionRepository.save(currentQuestion);

        int nextQuestionNumber = currentQuestion.getQuestionNumber() + 1;

        /*
         * Check if interview reached maximum questions (completed)
         */
        if (currentQuestion.getQuestionNumber() >= MAX_QUESTIONS || nextQuestionNumber > MAX_QUESTIONS) {
            interview.setStatus("COMPLETED");
            interview.setEndedAt(LocalDateTime.now());
            interviewRepository.save(interview);

            return new AIInterviewResponse(
                    interview.getId(),
                    currentQuestion.getQuestionNumber(),
                    "Thank you very much. That completes your technical interview! You can now view your comprehensive AI evaluation report.",
                    "",
                    "COMPLETED",
                    true
            );
        }

        /*
         * Build the conversation history
         */
        String conversation = buildConversation(questions);

        Resume resume = interview.getResume();
        Job job = interview.getJob();

        String resumeText = (resume != null && resume.getParsedText() != null && !resume.getParsedText().isBlank())
                ? resume.getParsedText()
                : "Candidate technical profile";

        String prompt = buildNextQuestionPrompt(
                resumeText,
                job,
                conversation,
                nextQuestionNumber,
                questions
        );

        String generatedQuestion = "";
        try {
            String rawQuestion = llmService.generateResponse(prompt);
            generatedQuestion = cleanQuestion(rawQuestion);
        } catch (Exception ignored) {
        }

        /*
         * DEDUPLICATION SAFEGUARD:
         * If the question is blank or is a duplicate of ANY question already asked,
         * use the curated topic fallback question for this question number.
         */
        if (generatedQuestion.isBlank() || isDuplicateQuestion(generatedQuestion, questions)) {
            generatedQuestion = getTopicFallbackQuestion(nextQuestionNumber, job);
        }

        InterviewQuestion nextQuestion = new InterviewQuestion();
        nextQuestion.setInterview(interview);
        nextQuestion.setQuestionNumber(nextQuestionNumber);
        nextQuestion.setQuestion(generatedQuestion.trim());
        nextQuestion.setAskedAt(LocalDateTime.now());
        interviewQuestionRepository.save(nextQuestion);

        String transitionMessage = nextQuestionNumber == 2
                ? "Okay, thank you. Now I will ask you some technical questions."
                : getTransitionMessage(currentQuestion.getQuestionNumber());

        return new AIInterviewResponse(
                interview.getId(),
                nextQuestionNumber,
                transitionMessage,
                generatedQuestion.trim(),
                interview.getStatus(),
                false
        );
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
        return buildNextQuestionPrompt(resumeText, job, previousConversation, 2, List.of());
    }

    private String buildNextQuestionPrompt(
            String resumeText,
            Job job,
            String previousConversation,
            int nextQuestionNumber,
            List<InterviewQuestion> existingQuestions
    ) {
        StringBuilder excludedList = new StringBuilder();
        if (existingQuestions != null) {
            for (InterviewQuestion eq : existingQuestions) {
                if (eq.getQuestion() != null && !eq.getQuestion().isBlank()) {
                    excludedList.append("- ").append(eq.getQuestion().trim()).append("\n");
                }
            }
        }

        String topicGuidance = getTopicGuidance(nextQuestionNumber);

        String resumeSummary = (resumeText != null && resumeText.length() > 300)
                ? resumeText.substring(0, 300) + "..."
                : (resumeText != null ? resumeText : "Software candidate");

        String jobDescSnippet = (job.getDescription() != null && job.getDescription().length() > 200)
                ? job.getDescription().substring(0, 200) + "..."
                : (job.getDescription() != null ? job.getDescription() : "");

        String prevContextSnippet = (previousConversation != null && previousConversation.length() > 250)
                ? previousConversation.substring(previousConversation.length() - 250)
                : (previousConversation != null ? previousConversation : "Beginning of technical interview.");

        return """
                TASK: Ask Question %d of 10 for a technical interview.

                TOPIC: %s

                RULES:
                - Return ONLY ONE short technical question (6-12 words).
                - End with "?". No intro, no markdown, no quotes.
                - DO NOT ask any question from the excluded list!

                EXCLUDED QUESTIONS:
                %s

                Candidate Skills / Role: %s | Target Job: %s at %s | Required: %s

                Generate single Question %d now:
                """.formatted(
                nextQuestionNumber,
                topicGuidance,
                excludedList.toString().isBlank() ? "- None" : excludedList.toString(),
                resumeSummary,
                job.getTitle(),
                job.getCompanyName(),
                job.getRequiredSkills(),
                nextQuestionNumber
        );
    }


    private String getTopicGuidance(int questionNumber) {
        return switch (questionNumber) {
            case 2 -> "TOPIC: CORE PROGRAMMING & OOP. Ask about fundamental OOP concepts (polymorphism, inheritance, encapsulation), collections, memory management, or core language fundamentals relevant to the candidate's skills.";
            case 3 -> "TOPIC: FRAMEWORK & ARCHITECTURE. Ask about application frameworks (such as Spring Boot dependency injection, bean scopes, autoconfiguration, or application configuration).";
            case 4 -> "TOPIC: DATABASE & PERSISTENCE. Ask about database design, indexing, SQL queries, transactions (ACID properties), or JPA/Hibernate ORM.";
            case 5 -> "TOPIC: API DESIGN & PROTOCOLS. Ask about RESTful principles, HTTP methods (GET/POST/PUT/DELETE/PATCH), status codes, or API best practices.";
            case 6 -> "TOPIC: SECURITY & AUTHENTICATION. Ask about authentication vs authorization, JWT tokens, Spring Security filter chains, or secure password storage.";
            case 7 -> "TOPIC: CONCURRENCY & MULTITHREADING. Ask about thread safety, synchronized blocks, executor services, connection pools, or race conditions.";
            case 8 -> "TOPIC: TESTING & CODE QUALITY. Ask about unit testing, Mockito mocking, test coverage, or SOLID principles.";
            case 9 -> "TOPIC: SYSTEM DESIGN & PERFORMANCE. Ask about caching strategies (e.g., Redis), handling high traffic, pagination, or database connection pooling.";
            case 10 -> "TOPIC: TROUBLESHOOTING & REAL-WORLD SCENARIO. Ask how the candidate diagnoses and debugs a complex production issue (like high CPU, memory leaks, or slow queries).";
            default -> "TOPIC: ADVANCED TECHNICAL CONCEPTS. Ask a focused technical question relevant to the job skills that has not been asked yet.";
        };
    }

    private String getTopicFallbackQuestion(int questionNumber, Job job) {
        return switch (questionNumber) {
            case 2 -> "What is the difference between an interface and an abstract class in Java?";
            case 3 -> "How does dependency injection work in Spring Boot?";
            case 4 -> "What is the difference between optimistic and pessimistic locking in JPA?";
            case 5 -> "What is the difference between PUT and PATCH in RESTful APIs?";
            case 6 -> "How does JWT authentication work and what are its main components?";
            case 7 -> "How do you achieve thread safety in Java concurrent applications?";
            case 8 -> "What is the difference between a unit test and an integration test?";
            case 9 -> "How would you design a caching strategy to handle high traffic?";
            case 10 -> "How do you troubleshoot and diagnose a memory leak in a Java application?";
            default -> "What are the key best practices you follow when writing clean, maintainable code?";
        };
    }

    private String cleanQuestion(String raw) {
        if (raw == null) return "";
        String q = raw.trim();
        // Remove markdown backticks and quotes
        q = q.replaceAll("`", "");
        if ((q.startsWith("\"") && q.endsWith("\"")) || (q.startsWith("'") && q.endsWith("'"))) {
            q = q.substring(1, q.length() - 1).trim();
        }
        // Remove prefixes like "Question 3:", "Question:", "Next question:"
        q = q.replaceAll("^(?i)(question\\s*\\d*\\s*:\\s*|next\\s*question\\s*:\\s*)", "").trim();
        if (!q.endsWith("?")) {
            q = q + "?";
        }
        return q;
    }

    private boolean isDuplicateQuestion(String question, List<InterviewQuestion> existingQuestions) {
        if (question == null || question.isBlank() || existingQuestions == null) {
            return true;
        }
        String normQ = question.toLowerCase().replaceAll("[^a-z0-9]", "");
        for (InterviewQuestion eq : existingQuestions) {
            if (eq.getQuestion() != null) {
                String normExisting = eq.getQuestion().toLowerCase().replaceAll("[^a-z0-9]", "");
                if (normQ.equals(normExisting) ||
                        (normQ.length() > 18 && normExisting.contains(normQ)) ||
                        (normExisting.length() > 18 && normQ.contains(normExisting))) {
                    return true;
                }
            }
        }
        return false;
    }
}

