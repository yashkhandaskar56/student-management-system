package com.example.controller;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.CourseDto;
import com.example.entity.CourseList;
import com.example.service.AdmissionSer;
import com.example.service.CourseSer;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/course")
public class CourseCo {
	
	@Autowired
	private CourseSer ser;
	
	@Autowired
	private AdmissionSer admissionser;
	
	@PostMapping("/add")
	public String addCourse(@RequestBody CourseDto dto) {
		return ser.addCourse(dto);
	}
	
	@GetMapping("/get")
	public List<CourseList> getAllCourse(){
		return ser.getAllCourse();
	}
	
	@PutMapping("/edit/{id}")
    public String editCourse(@PathVariable Long id, @RequestBody CourseDto dto) {
        return ser.updateCourse(id, dto);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {
        return ser.deleteCourse(id);
    }

	@GetMapping("/dashboard-summary")
	public ResponseEntity<Map<String, Object>> getDashBordsummary(){
		Map<String , Object> summary=new HashMap<>();
		summary.put("totalCourses", ser.gettotalCourses());
		summary.put("freeCourses", ser.getfreeCourses());
		summary.put("paidCourses", ser.getpaidCourses());
		summary.put("totaladmission", admissionser.gettotalAdmission());
		
		return ResponseEntity.ok(summary);
		
	}
	
	@PostMapping("/complete")
	public String completeCourse(@RequestParam String email,
	                             @RequestParam String courseName){

	    ser.markCourseCompleted(email, courseName);
	    return "Course marked as COMPLETED";
	}
	
	@PutMapping("/update-status")
	public ResponseEntity<String> updateCourseStatus(

	        @RequestParam String email,
	        @RequestParam String courseName){

	    ser.updateCourseStatus(email, courseName);

	    return ResponseEntity.ok("Course status updated");
	}
	
}
