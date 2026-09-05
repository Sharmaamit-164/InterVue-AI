package com.intervueai.backend.ai.interview;

import com.intervueai.backend.job.entity.Job;
import com.intervueai.backend.job.repository.JobRepository;
import com.intervueai.backend.resume.entity.Resume;
import com.intervueai.backend.resume.repository.ResumeRepository;
import com.intervueai.backend.user.entity.User;
import com.intervueai.backend.user.repository.UserRepository;
import com.intervueai.backend.ai.llm.LLMService;
import org.springframework.stereotype.Service;

@Service
public class AIInterviewServiceImpl implements AIInterviewService {

    private final LLMService llmService;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;

    public AIInterviewServiceImpl(
            LLMService llmService,
            UserRepository userRepository,
            ResumeRepository resumeRepository,
            JobRepository jobRepository
    ) {
        this.llmService = llmService;
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
    }

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
                        new RuntimeException("Resume not found for the logged-in user")
                );

        Job job = jobRepository
                .findByIdAndUser(jobId, user)
                .orElseThrow(() ->
                        new RuntimeException("Job not found for the logged-in user")
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
                        new RuntimeException("Resume not found for the logged-in user")
                );

        Job job = jobRepository
                .findByIdAndUser(jobId, user)
                .orElseThrow(() ->
                        new RuntimeException("Job not found for the logged-in user")
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

    private User findUser(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
    }

    private String buildFirstQuestionPrompt(
            String resumeText,
            Job job
    ) {

        return """
                You are a professional AI interviewer.

                Conduct a realistic job interview for the candidate.

                Your task is to generate ONLY the first interview question.

                Rules:
                - Ask exactly ONE question.
                - Do not provide the answer.
                - Do not explain the question.
                - Do not provide multiple questions.
                - Do not mention that you are an AI.
                - Use the candidate's resume and the job description.
                - The question must be relevant to the target job.
                - Prefer asking about something specific from the candidate's
                  resume rather than asking a generic question.

                Candidate Resume:
                %s

                Job Title:
                %s

                Company:
                %s

                Job Description:
                %s

                Required Skills:
                %s

                Generate the first interview question now.
                """.formatted(
                resumeText,
                job.getTitle(),
                job.getCompanyName(),
                job.getDescription(),
                job.getRequiredSkills()
        );
    }

    private String buildNextQuestionPrompt(
            String resumeText,
            Job job,
            String previousConversation
    ) {

        return """
                You are a professional AI interviewer conducting
                a dynamic job interview.

                Generate ONLY the next interview question.

                Rules:
                - Ask exactly ONE question.
                - Never provide the answer.
                - Never explain the question.
                - Never ask multiple questions.
                - Never repeat a previous question.
                - Use the candidate's resume and target job.
                - Consider the complete previous conversation.
                - Adapt the difficulty according to the candidate's previous answer.
                - Ask a follow-up question when the previous answer
                  contains something worth exploring.
                - Move to another relevant topic when appropriate.
                - Keep the question relevant to the target job.

                Candidate Resume:
                %s

                Job Title:
                %s

                Company:
                %s

                Job Description:
                %s

                Required Skills:
                %s

                Previous Interview Conversation:
                %s

                Generate the single best next interview question.
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