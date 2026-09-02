package com.intervueai.backend.job.service;

import com.intervueai.backend.job.dto.CreateJobRequest;
import com.intervueai.backend.job.dto.JobResponse;
import com.intervueai.backend.job.entity.Job;
import com.intervueai.backend.job.repository.JobRepository;
import com.intervueai.backend.user.entity.User;
import com.intervueai.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobServiceImpl(
            JobRepository jobRepository,
            UserRepository userRepository
    ) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    // =========================
    // Create Job
    // =========================

    @Override
    @Transactional
    public JobResponse createJob(
            String email,
            CreateJobRequest request
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (request == null) {
            throw new IllegalArgumentException(
                    "Job request cannot be null"
            );
        }

        if (request.getTitle() == null ||
                request.getTitle().isBlank()) {

            throw new IllegalArgumentException(
                    "Job title cannot be empty"
            );
        }

        if (request.getDescription() == null ||
                request.getDescription().isBlank()) {

            throw new IllegalArgumentException(
                    "Job description cannot be empty"
            );
        }

        if (request.getCompanyName() == null ||
                request.getCompanyName().isBlank()) {

            throw new IllegalArgumentException(
                    "Company name cannot be empty"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        Job job = new Job();

        job.setUser(user);
        job.setTitle(request.getTitle());
        job.setCompanyName(request.getCompanyName());
        job.setDescription(request.getDescription());
        job.setRequiredSkills(request.getRequiredSkills());
        job.setCreatedAt(now);
        job.setUpdatedAt(now);

        Job savedJob = jobRepository.save(job);

        return convertToResponse(savedJob);
    }

    // =========================
    // Get My Jobs
    // =========================

    @Override
    public List<JobResponse> getMyJobs(
            String email
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return jobRepository.findByUser(user)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================
    // Get Single Job
    // =========================

    @Override
    public JobResponse getMyJob(
            String email,
            Long jobId
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Job job = jobRepository
                .findByIdAndUser(jobId, user)
                .orElseThrow(() ->
                        new RuntimeException("Job not found"));

        return convertToResponse(job);
    }

    // =========================
    // Update Job
    // =========================

    @Override
    @Transactional
    public JobResponse updateJob(
            String email,
            Long jobId,
            CreateJobRequest request
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Job job = jobRepository
                .findByIdAndUser(jobId, user)
                .orElseThrow(() ->
                        new RuntimeException("Job not found"));

        if (request == null) {
            throw new IllegalArgumentException(
                    "Job request cannot be null"
            );
        }

        if (request.getTitle() == null ||
                request.getTitle().isBlank()) {

            throw new IllegalArgumentException(
                    "Job title cannot be empty"
            );
        }

        if (request.getCompanyName() == null ||
                request.getCompanyName().isBlank()) {

            throw new IllegalArgumentException(
                    "Company name cannot be empty"
            );
        }

        if (request.getDescription() == null ||
                request.getDescription().isBlank()) {

            throw new IllegalArgumentException(
                    "Job description cannot be empty"
            );
        }

        job.setTitle(request.getTitle());
        job.setCompanyName(request.getCompanyName());
        job.setDescription(request.getDescription());
        job.setRequiredSkills(request.getRequiredSkills());
        job.setUpdatedAt(LocalDateTime.now());

        Job updatedJob = jobRepository.save(job);

        return convertToResponse(updatedJob);
    }

    // =========================
    // Delete Job
    // =========================

    @Override
    @Transactional
    public void deleteMyJob(
            String email,
            Long jobId
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Job job = jobRepository
                .findByIdAndUser(jobId, user)
                .orElseThrow(() ->
                        new RuntimeException("Job not found"));

        jobRepository.delete(job);
    }

    // =========================
    // Convert Entity → Response
    // =========================

    private JobResponse convertToResponse(
            Job job
    ) {

        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getCompanyName(),
                job.getDescription(),
                job.getRequiredSkills(),
                job.getCreatedAt(),
                job.getUpdatedAt()
        );
    }
}