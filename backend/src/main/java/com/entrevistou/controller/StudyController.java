package com.entrevistou.controller;

import com.entrevistou.dto.LearningPathDto;
import com.entrevistou.dto.StudySessionDto;
import com.entrevistou.dto.StudySessionResultDto;
import com.entrevistou.dto.SubmitSessionRequest;
import com.entrevistou.model.StudyTopic;
import com.entrevistou.service.StudySessionService;
import com.entrevistou.service.StudyTopicService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class StudyController {
    private final StudyTopicService topicService;
    private final StudySessionService sessionService;

    public StudyController(StudyTopicService topicService, StudySessionService sessionService) {
        this.topicService = topicService;
        this.sessionService = sessionService;
    }

    @GetMapping("/topics")
    public List<StudyTopic> topics() {
        return topicService.findAll();
    }

    @PostMapping("/topics")
    public StudyTopic create(@RequestBody StudyTopic topic) {
        return topicService.save(topic);
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return Map.of("readiness", Math.round(topicService.readiness()), "topics", topicService.findAll());
    }

    @GetMapping("/learning-path")
    public LearningPathDto learningPath() {
        return topicService.learningPath();
    }

    @GetMapping("/study/{slug}")
    public StudySessionDto studySession(@PathVariable String slug) {
        try {
            return sessionService.getSession(slug);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    @PostMapping("/study/{slug}/submit")
    public StudySessionResultDto submitSession(@PathVariable String slug,
                                               @RequestBody SubmitSessionRequest request) {
        try {
            return sessionService.submit(slug, request);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }
}
