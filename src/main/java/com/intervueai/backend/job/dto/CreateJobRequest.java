package com.intervueai.backend.job.dto;

public class CreateJobRequest {

    private String title;
    private String companyName;
    private String description;
    private String requiredSkills;

    public CreateJobRequest() {
    }

    public CreateJobRequest(
            String title,
            String companyName,
            String description,
            String requiredSkills
    ) {
        this.title = title;
        this.companyName = companyName;
        this.description = description;
        this.requiredSkills = requiredSkills;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }
}