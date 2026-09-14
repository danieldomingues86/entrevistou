package com.entrevistou.dto;

import java.util.List;

public record StudyQuestionDto(
        String id,
        String prompt,
        String difficulty,
        List<QuestionOptionDto> options
) {}
