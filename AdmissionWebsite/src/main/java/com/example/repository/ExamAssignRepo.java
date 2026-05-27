package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.entity.ExamAssign;
import com.example.entity.ExamEntity;

public interface ExamAssignRepo extends JpaRepository<ExamAssign, Long>{

	boolean existsByStudentEmailIgnoreCaseAndExam_ExamId(String email, Long examId);

	List<ExamAssign> findByStudentEmailIgnoreCaseAndAvailableTrue(String email);

	Optional<ExamAssign> findByStudentEmailIgnoreCaseAndExam_ExamId(String email, Long examId);

	
}
