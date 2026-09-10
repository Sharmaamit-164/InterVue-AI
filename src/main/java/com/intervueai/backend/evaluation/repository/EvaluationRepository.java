package com.intervueai.backend.evaluation.repository;

import com.intervueai.backend.evaluation.entity.Evaluation;
import com.intervueai.backend.interview.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    Optional<Evaluation> findByInterview(Interview interview);

}