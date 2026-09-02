package com.intervueai.backend.matching.engine;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component
public class ResumeJobMatcher {

    /**
     * Compares the required skills with the resume text.
     *
     * @param resumeText     Text extracted from the uploaded resume
     * @param requiredSkills Skills required by the job
     * @return MatchingResult containing score, matched skills and missing skills
     */
    public MatchingResult match(
            String resumeText,
            String requiredSkills
    ) {

        if (resumeText == null || resumeText.isBlank()) {
            throw new IllegalArgumentException(
                    "Resume text cannot be empty"
            );
        }

        if (requiredSkills == null || requiredSkills.isBlank()) {
            throw new IllegalArgumentException(
                    "Required skills cannot be empty"
            );
        }

        String normalizedResume =
                resumeText.toLowerCase(Locale.ROOT);

        List<String> skills =
                Arrays.stream(requiredSkills.split(","))
                        .map(String::trim)
                        .filter(skill -> !skill.isBlank())
                        .toList();

        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String skill : skills) {

            String normalizedSkill =
                    skill.toLowerCase(Locale.ROOT);

            if (normalizedResume.contains(normalizedSkill)) {
                matchedSkills.add(skill);
            } else {
                missingSkills.add(skill);
            }
        }

        double matchScore = 0.0;

        if (!skills.isEmpty()) {
            matchScore =
                    ((double) matchedSkills.size()
                            / skills.size()) * 100;
        }

        return new MatchingResult(
                Math.round(matchScore * 100.0) / 100.0,
                matchedSkills,
                missingSkills
        );
    }

    /**
     * Result returned by the matching engine.
     */
    public static class MatchingResult {

        private final double matchScore;
        private final List<String> matchedSkills;
        private final List<String> missingSkills;

        public MatchingResult(
                double matchScore,
                List<String> matchedSkills,
                List<String> missingSkills
        ) {
            this.matchScore = matchScore;
            this.matchedSkills = matchedSkills;
            this.missingSkills = missingSkills;
        }

        public double getMatchScore() {
            return matchScore;
        }

        public List<String> getMatchedSkills() {
            return matchedSkills;
        }

        public List<String> getMissingSkills() {
            return missingSkills;
        }
    }
}