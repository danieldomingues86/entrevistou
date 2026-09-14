package com.entrevistou.repository;

import com.entrevistou.model.StudyTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyTopicRepository extends JpaRepository<StudyTopic, Long> {
    Optional<StudyTopic> findByTitle(String title);
}
