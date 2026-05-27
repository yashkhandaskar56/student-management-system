package com.example.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.LoginDto;
import com.example.dto.RegisterDto;
import com.example.dto.StudentProfileDto;
import com.example.entity.AdmissionApplication;
import com.example.entity.Student;
import com.example.service.StudentSer;

@RestController
@RequestMapping("/student")
@CrossOrigin("*")
public class StudentCon {
	
	@Autowired
	private StudentSer ser;
	
	
	@PostMapping("/register")
	public String registerStudent(@RequestBody RegisterDto request) {
		return ser.registerStudent(request);
	}
	
	@GetMapping("/register-get")
	public List<Student> getAll(){
		return ser.getAll();
	}
	
	@PostMapping("/login")
	public String loginStudent(@RequestBody LoginDto request) {
		return ser.loginStudent(request);
	}
	
	@PostMapping("/forgetpass")
	public String resetPass(@RequestBody LoginDto dto) {
		return ser.resetPass(dto);
	}
	
	@GetMapping("/dashboard/{email}")
	public List<AdmissionApplication> getStudentAdmission(@PathVariable String email){
		return ser.getStudentAdmission(email);
	}
	
	@GetMapping("/profile")
	public StudentProfileDto getstudentByEmail(@RequestParam String email) {
		return ser.getStudentProfileByEmail(email);
	}

}
