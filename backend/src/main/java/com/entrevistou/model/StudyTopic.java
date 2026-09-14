package com.entrevistou.model;

import jakarta.persistence.*;

@Entity
@Table(name = "study_topics")
public class StudyTopic {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String category;
    private String title;
    private int mastery;
    private int priority;

    public StudyTopic() {}
    public StudyTopic(String category, String title, int mastery, int priority) {
        this.category = category; this.title = title; this.mastery = mastery; this.priority = priority;
    }
    public Long getId() { return id; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getMastery() { return mastery; }
    public void setMastery(int mastery) { this.mastery = mastery; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
}
