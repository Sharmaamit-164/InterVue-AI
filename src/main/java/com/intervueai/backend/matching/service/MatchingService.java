package com.intervueai.backend.matching.service;

import com.intervueai.backend.matching.dto.MatchingResponse;

public interface MatchingService {

    MatchingResponse matchResumeWithJob(
            String email,
            Long resumeId,
            Long jobId
    );
}