package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.entity.AdmissionApplication;
import com.example.entity.CourseList;

public interface CourseRepo extends JpaRepository<CourseList, Long>{

	 Optional<CourseList> findByCourseName(String courseName);
	 
	 @Query("SELECT COUNT (c) FROM CourseList c")
	 Long gettotalCourses();
	 
	 @Query("SELECT COUNT (c) FROM CourseList c WHERE c.free=0")
	Long getfreeCourses();
	 
	 @Query("SELECT COUNT (c) FROM CourseList c WHERE c.free>0")
	 Long getpaidCourses();
	 
	 long count();


	

}
