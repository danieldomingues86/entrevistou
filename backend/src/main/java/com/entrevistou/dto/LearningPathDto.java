package com.entrevistou.dto;

import java.util.List;

public record LearningPathDto(
        String title,
        String level,
        int readiness,
        List<LearningModuleDto> modules
) {}
