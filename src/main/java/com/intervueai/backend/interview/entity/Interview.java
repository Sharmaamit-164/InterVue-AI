package com.intervueai.backend.interview.entity;

import com.intervueai.backend.job.entity.Job;
import com.intervueai.backend.resume.entity.Resume;
import com.intervueai.backend.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "interviews")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User who is taking the interview
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Resume used for this interview
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    // Job selected for this interview
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    // Interview status
    @Column(nullable = false)
    private String status;

    // Interview start time
    @Column(nullable = false)
    private LocalDateTime startedAt;

    // Interview end time
    private LocalDateTime endedAt;

    // Questions belonging to this interview
    @OneToMany(
            mappedBy = "interview",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("questionNumber ASC")
    private List<InterviewQuestion> questions = new ArrayList<>();

    public Interview() {
    }

    public Interview(
            User user,
            Resume resume,
            Job job,
            String status,
            LocalDateTime startedAt,
            LocalDateTime endedAt
    ) {
        this.user = user;
        this.resume = resume;
        this.job = job;
        this.status = status;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Resume getResume() {
        return resume;
    }

    public Job getJob() {
        return job;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public List<InterviewQuestion> getQuestions() {
        return questions;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public void setQuestions(List<InterviewQuestion> questions) {
        this.questions = questions;
    }

    public void addQuestion(InterviewQuestion question) {
        questions.add(question);
        question.setInterview(this);
    }

    public void removeQuestion(InterviewQuestion question) {
        questions.remove(question);
        question.setInterview(null);
    }
}