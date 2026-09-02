package com.intervueai.backend.matching.service;

import com.intervueai.backend.job.entity.Job;
import com.intervueai.backend.job.repository.JobRepository;
import com.intervueai.backend.matching.dto.MatchingResponse;
import com.intervueai.backend.matching.engine.ResumeJobMatcher;
import com.intervueai.backend.resume.entity.Resume;
import com.intervueai.backend.resume.repository.ResumeRepository;
import com.intervueai.backend.user.entity.User;
import com.intervueai.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class MatchingServiceImpl implements MatchingService {

    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ResumeJobMatcher resumeJobMatcher;

    public MatchingServiceImpl(
            ResumeRepository resumeRepository,
            JobRepository jobRepository,
            UserRepository userRepository,
            ResumeJobMatcher resumeJobMatcher
    ) {
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.resumeJobMatcher = resumeJobMatcher;
    }

    @Override
    public MatchingResponse matchResumeWithJob(
            String email,
            Long resumeId,
            Long jobId
    ) {

        // =========================
        // Find logged-in user
        // =========================

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // =========================
        // Find user's resume
        // =========================

        Resume resume = resumeRepository
                .findByIdAndUser(resumeId, user)
                .orElseThrow(() ->
                        new RuntimeException("Resume not found")
                );

        // =========================
        // Find job
        // =========================

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new RuntimeException("Job not found")
                );

        // =========================
        // Get resume text
        // =========================

        String resumeText = resume.getParsedText();

        // =========================
        // Get required job skills
        // =========================

        String requiredSkills = job.getRequiredSkills();

        // =========================
        // Perform matching
        // =========================

        ResumeJobMatcher.MatchingResult result =
                resumeJobMatcher.match(
                        resumeText,
                        requiredSkills
                );

        // =========================
        // Return matching response
        // =========================

        return new MatchingResponse(
                resume.getId(),
                job.getId(),
                result.getMatchScore(),
                result.getMatchedSkills(),
                result.getMissingSkills()
        );
    }
}