package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.ExamAssignSer;

@RestController
@RequestMapping("/exam-assign")

public class ExamAssignCon {

	@Autowired
	private ExamAssignSer ser;
	
	
	@PostMapping("/student")
	public ResponseEntity<?> assignExam(
	        @RequestParam Long examId,
	        @RequestParam String email) {

	    return ResponseEntity.ok(
	            ser.assignExamToStudent(examId, email)
	    );
	}
	
}
