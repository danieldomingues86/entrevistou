package com.entrevistou.config;

import com.entrevistou.model.StudyTopic;
import com.entrevistou.repository.StudyTopicRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedDataConfig {
    @Bean
    CommandLineRunner seed(StudyTopicRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new StudyTopic("Java Core", "Collections & Generics", 74, 1));
                repo.save(new StudyTopic("Java Core", "JVM & Concorrência", 68, 1));
                repo.save(new StudyTopic("Spring", "Spring Boot & DI", 80, 1));
                repo.save(new StudyTopic("Spring", "REST, JPA & Transactions", 71, 1));
                repo.save(new StudyTopic("Kafka", "Consumer Groups & Offsets", 59, 1));
                repo.save(new StudyTopic("SQL", "Indexes & Transactions", 76, 1));
                repo.save(new StudyTopic("System Design", "Scalability & Resilience", 62, 1));
                repo.save(new StudyTopic("Mock Interview", "Senior Backend Interview", 55, 1));
            }
        };
    }
}
