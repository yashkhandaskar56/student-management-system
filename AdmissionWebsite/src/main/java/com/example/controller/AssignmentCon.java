package com.example.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.entity.Assignment;
import com.example.service.AssignmentSer;

@RestController
@RequestMapping("/assignment")
public class AssignmentCon {
	
	@Autowired
	private AssignmentSer ser;
	
	// Admin: Create assignment
	@PostMapping("/create")
	public Assignment createAssignmnet(@RequestBody Assignment a) {
		return ser.createAssignment(a);
	}
	
	@GetMapping("/get/student-Assignmnet")
	public List<Assignment> getAssignmnet(@RequestParam String email){
		return ser.getAssignmentByStudent(email);
	}
	
	@PostMapping("/student/assignmnet-submit")
	public Assignment submitAssignmnet(@RequestParam Long id,
			@RequestParam(required = false) String submissionText,
			@RequestParam (required = false) MultipartFile file) throws IllegalStateException, IOException {
		return ser.submitAssignmnet(id,submissionText,file);
	}
	
	

}
