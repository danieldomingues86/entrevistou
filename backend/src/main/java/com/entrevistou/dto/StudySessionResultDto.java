package com.entrevistou.dto;

import java.util.List;

public record StudySessionResultDto(
        int correct,
        int total,
        int score,
        int previousMastery,
        int newMastery,
        String message,
        List<QuestionResultDto> results
) {}
