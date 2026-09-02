package com.intervueai.backend.matching.dto;

import java.util.List;

public class MatchingResponse {

    private Long resumeId;
    private Long jobId;

    private double matchScore;

    private List<String> matchedSkills;
    private List<String> missingSkills;

    public MatchingResponse() {
    }

    public MatchingResponse(
            Long resumeId,
            Long jobId,
            double matchScore,
            List<String> matchedSkills,
            List<String> missingSkills
    ) {
        this.resumeId = resumeId;
        this.jobId = jobId;
        this.matchScore = matchScore;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(double matchScore) {
        this.matchScore = matchScore;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }
}