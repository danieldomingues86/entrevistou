package com.entrevistou.dto;

public record LearningModuleDto(
        String category,
        String description,
        int mastery,
        int topicCount,
        String status,
        String featuredSessionSlug
) {}
