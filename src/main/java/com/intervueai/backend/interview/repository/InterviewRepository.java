package com.intervueai.backend.interview.repository;

import com.intervueai.backend.interview.entity.Interview;
import com.intervueai.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByUser(User user);

    Optional<Interview> findByIdAndUser(Long id, User user);
}