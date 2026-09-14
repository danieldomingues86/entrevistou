package com.entrevistou.dto;

public record QuestionResultDto(
        String questionId,
        boolean correct,
        String correctOptionId,
        String explanation
) {}
