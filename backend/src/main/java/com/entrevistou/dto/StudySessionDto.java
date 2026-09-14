package com.entrevistou.dto;

import java.util.List;

public record StudySessionDto(
        String slug,
        String category,
        String title,
        String subtitle,
        int estimatedMinutes,
        int mastery,
        List<StudySectionDto> sections,
        List<StudyQuestionDto> questions
) {}
