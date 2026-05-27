package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.entity.ExamEntity;

public interface ExamRepo extends JpaRepository<ExamEntity, Long>{

	List<ExamEntity> findByCourseName(String courseName);

	List<ExamEntity> findByCourseNameIn(List<String> courseNames);

	@Query("SELECT COUNT(e) FROM ExamEntity e")
    Long countTotalExams( );
	
	long count();

	

	
}
