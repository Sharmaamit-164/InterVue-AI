package com.intervueai.backend.job.service;

import com.intervueai.backend.job.dto.CreateJobRequest;
import com.intervueai.backend.job.dto.JobResponse;

import java.util.List;

public interface JobService {

    // Create a new job
    JobResponse createJob(
            String email,
            CreateJobRequest request
    );

    // Get all jobs of the logged-in user
    List<JobResponse> getMyJobs(
            String email
    );

    // Get a specific job of the logged-in user
    JobResponse getMyJob(
            String email,
            Long jobId
    );

    // Update a job of the logged-in user
    JobResponse updateJob(
            String email,
            Long jobId,
            CreateJobRequest request
    );

    // Delete a job of the logged-in user
    void deleteMyJob(
            String email,
            Long jobId
    );
}