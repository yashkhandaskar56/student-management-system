package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.SubmissionAnswerEntity;

public interface SubmissionAnsRepo extends JpaRepository<SubmissionAnswerEntity, Long>{

}
