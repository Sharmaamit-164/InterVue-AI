package com.intervueai.backend.job.controller;

import com.intervueai.backend.job.dto.CreateJobRequest;
import com.intervueai.backend.job.dto.JobResponse;
import com.intervueai.backend.job.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@SecurityRequirement(name = "bearerAuth")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // =========================
    // Create Job
    // =========================

    @Operation(
            summary = "Create Job",
            description = "Create a new job description for the logged-in user"
    )
    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            @RequestBody CreateJobRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        JobResponse response =
                jobService.createJob(email, request);

        return ResponseEntity.ok(response);
    }

    // =========================
    // Get My Jobs
    // =========================

    @Operation(
            summary = "Get My Jobs",
            description = "Get all jobs created by the logged-in user"
    )
    @GetMapping
    public ResponseEntity<List<JobResponse>> getMyJobs(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                jobService.getMyJobs(email)
        );
    }

    // =========================
    // Get Single Job
    // =========================

    @Operation(
            summary = "Get My Job",
            description = "Get a specific job belonging to the logged-in user"
    )
    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> getMyJob(
            @PathVariable Long jobId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                jobService.getMyJob(email, jobId)
        );
    }

    // =========================
    // Update Job
    // =========================

    @Operation(
            summary = "Update Job",
            description = "Update a job belonging to the logged-in user"
    )
    @PutMapping("/{jobId}")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable Long jobId,
            @RequestBody CreateJobRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        JobResponse response =
                jobService.updateJob(
                        email,
                        jobId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    // =========================
    // Delete Job
    // =========================

    @Operation(
            summary = "Delete Job",
            description = "Delete a job belonging to the logged-in user"
    )
    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteMyJob(
            @PathVariable Long jobId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        jobService.deleteMyJob(
                email,
                jobId
        );

        return ResponseEntity.noContent().build();
    }
}