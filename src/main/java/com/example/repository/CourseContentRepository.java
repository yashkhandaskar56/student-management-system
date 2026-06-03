package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.CourseContent;

public interface CourseContentRepository extends JpaRepository<CourseContent, Long>{

	List<CourseContent> findTop5ByOrderByUploadDateDesc();

}
