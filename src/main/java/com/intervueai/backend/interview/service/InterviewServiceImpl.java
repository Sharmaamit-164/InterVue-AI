package com.intervueai.backend.interview.service;

import com.intervueai.backend.interview.dto.CreateInterviewRequest;
import com.intervueai.backend.interview.dto.InterviewQuestionResponse;
import com.intervueai.backend.interview.dto.InterviewResponse;
import com.intervueai.backend.interview.entity.Interview;
import com.intervueai.backend.interview.entity.InterviewQuestion;
import com.intervueai.backend.interview.repository.InterviewQuestionRepository;
import com.intervueai.backend.interview.repository.InterviewRepository;
import com.intervueai.backend.job.entity.Job;
import com.intervueai.backend.job.repository.JobRepository;
import com.intervueai.backend.resume.entity.Resume;
import com.intervueai.backend.resume.repository.ResumeRepository;
import com.intervueai.backend.user.entity.User;
import com.intervueai.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;

    public InterviewServiceImpl(
            InterviewRepository interviewRepository,
            InterviewQuestionRepository interviewQuestionRepository,
            UserRepository userRepository,
            ResumeRepository resumeRepository,
            JobRepository jobRepository
    ) {
        this.interviewRepository = interviewRepository;
        this.interviewQuestionRepository = interviewQuestionRepository;
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
    }

    @Override
    @Transactional
    public InterviewResponse createInterview(
            String email,
            CreateInterviewRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Interview request cannot be null"
            );
        }

        if (request.getResumeId() == null) {
            throw new IllegalArgumentException(
                    "Resume ID is required"
            );
        }

        if (request.getJobId() == null) {
            throw new IllegalArgumentException(
                    "Job ID is required"
            );
        }

        User user = findUser(email);

        Resume resume = resumeRepository
                .findByIdAndUser(request.getResumeId(), user)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Resume not found for the logged-in user"
                        )
                );

        Job job = jobRepository
                .findByIdAndUser(request.getJobId(), user)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Job not found for the logged-in user"
                        )
                );

        LocalDateTime now = LocalDateTime.now();

        Interview interview = new Interview();

        interview.setUser(user);
        interview.setResume(resume);
        interview.setJob(job);
        interview.setStatus("CREATED");
        interview.setStartedAt(now);

        Interview savedInterview =
                interviewRepository.save(interview);

        return convertToResponse(savedInterview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewResponse> getMyInterviews(
            String email
    ) {

        User user = findUser(email);

        return interviewRepository
                .findByUser(user)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewResponse getMyInterview(
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

        return convertToResponse(interview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewQuestionResponse> getInterviewQuestions(
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

        return interviewQuestionRepository
                .findByInterviewOrderByQuestionNumberAsc(interview)
                .stream()
                .map(this::convertToQuestionResponse)
                .toList();
    }

    @Override
    @Transactional
    public InterviewResponse completeInterview(
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
            return convertToResponse(interview);
        }

        interview.setStatus("COMPLETED");
        interview.setEndedAt(LocalDateTime.now());

        Interview updatedInterview =
                interviewRepository.save(interview);

        return convertToResponse(updatedInterview);
    }

    private User findUser(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
    }

    private InterviewResponse convertToResponse(
            Interview interview
    ) {

        return new InterviewResponse(
                interview.getId(),
                interview.getResume().getId(),
                interview.getJob().getId(),
                interview.getStatus(),
                interview.getStartedAt(),
                interview.getEndedAt()
        );
    }

    private InterviewQuestionResponse convertToQuestionResponse(
            InterviewQuestion question
    ) {

        return new InterviewQuestionResponse(
                question.getId(),
                question.getInterview().getId(),
                question.getQuestionNumber(),
                question.getQuestion(),
                question.getCandidateAnswer(),
                question.getAskedAt(),
                question.getAnsweredAt()
        );
    }
}