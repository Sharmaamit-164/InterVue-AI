package com.intervueai.backend.job.repository;

import com.intervueai.backend.job.entity.Job;
import com.intervueai.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {

    // Get all jobs created by a specific user
    List<Job> findByUser(User user);

    // Get a specific job belonging to a specific user
    Optional<Job> findByIdAndUser(Long id, User user);
}