package com.entrevistou.service;

import com.entrevistou.dto.LearningModuleDto;
import com.entrevistou.dto.LearningPathDto;
import com.entrevistou.model.StudyTopic;
import com.entrevistou.repository.StudyTopicRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudyTopicService {
    private final StudyTopicRepository repository;

    public StudyTopicService(StudyTopicRepository repository) { this.repository = repository; }

    public List<StudyTopic> findAll() { return repository.findAll(); }
    public StudyTopic save(StudyTopic topic) { return repository.save(topic); }

    public double readiness() {
        return repository.findAll().stream().mapToInt(StudyTopic::getMastery).average().orElse(0.0);
    }

    public LearningPathDto learningPath() {
        Map<String, List<StudyTopic>> grouped = repository.findAll().stream()
                .collect(Collectors.groupingBy(StudyTopic::getCategory, LinkedHashMap::new, Collectors.toList()));

        List<LearningModuleDto> modules = new ArrayList<>();
        add(modules, grouped, "Java Core", "Linguagem, Collections, JVM e concorrência", "java-core-collections");
        add(modules, grouped, "Spring", "Spring Boot, REST, JPA, transações e testes", "spring-boot-senior");
        add(modules, grouped, "Kafka", "Eventos, consumer groups, offsets, retries e DLQ", "kafka-consumer-groups-offsets");
        add(modules, grouped, "SQL", "Índices, transações, PostgreSQL e performance", "sql-indexes-transactions");
        add(modules, grouped, "System Design", "Escalabilidade, resiliência e sistemas distribuídos", "system-design-scalability");
        add(modules, grouped, "Mock Interview", "Simulação técnica para nível Senior Backend", "mock-interview-senior-backend");

        return new LearningPathDto("Java Senior Backend", "Senior", (int)Math.round(readiness()), modules);
    }

    private void add(List<LearningModuleDto> target, Map<String, List<StudyTopic>> grouped,
                     String category, String description, String slug) {
        List<StudyTopic> topics = grouped.getOrDefault(category, List.of());
        int mastery = topics.isEmpty() ? 0 : (int)Math.round(topics.stream().mapToInt(StudyTopic::getMastery).average().orElse(0));
        target.add(new LearningModuleDto(category, description, mastery, topics.size(), "Disponível", slug));
    }
}
