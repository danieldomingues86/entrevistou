package com.entrevistou.dto;

import java.util.List;

public record StudySectionDto(
        String id,
        String title,
        String summary,
        List<String> bullets,
        String interviewTip
) {}
