package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

import com.example.entity.CourseMaterial;

public interface CourseMaterialRepo extends JpaRepository<CourseMaterial, Long> {

	List<CourseMaterial> findByCourseName(String courseName);

}
