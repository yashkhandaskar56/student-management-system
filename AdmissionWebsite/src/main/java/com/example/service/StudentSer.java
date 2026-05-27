package com.example.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.dto.CourseInfo;
import com.example.dto.LoginDto;
import com.example.dto.RegisterDto;
import com.example.dto.StudentProfileDto;
import com.example.entity.AdmissionApplication;
import com.example.entity.CourseList;
import com.example.entity.Role;
import com.example.entity.Student;
import com.example.repository.AdmissionRepo;
import com.example.repository.CourseRepo;
import com.example.repository.StudentRepo;

@Service
public class StudentSer {
	
	@Autowired
	private StudentRepo repo;
	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private AdmissionRepo arepo;
	@Autowired
	private CourseRepo crepo;
	
	public String registerStudent(RegisterDto request) {
		Optional<Student> exits=repo.findByEmail(request.getEmail());
		if(exits.isPresent()) {
			return "Email is Already Exits";
		}
		
		if(request.getEmail().trim().isEmpty()||request.getSname().trim().isEmpty()||request.getPassword().trim().isEmpty()) {
			return "All Filed can not be Null";
		}
		Student s=new Student();
		s.setSname(request.getSname());
		s.setEmail(request.getEmail());
		s.setPassword(encoder.encode(request.getPassword()));
		s.setRole(Role.STUDENT);
		repo.save(s);
		
		return "Student Register Successfully";
		
	}
	
	public String loginStudent(LoginDto request) {
	    if (request.getEmail() == null || request.getPassword() == null) {
	        return "Email or Password cannot be null";
	    }

	    Optional<Student> studentOpt = repo.findByEmail(request.getEmail());

	    if (studentOpt.isEmpty()) {
	        return "Email is invalid";
	    }

	    Student student = studentOpt.get(); 

	    if (!encoder.matches(request.getPassword(), student.getPassword())) {
	        return "Password is invalid";
	    }
	    
	 // 🔥 NEW LOGIC (IMPORTANT)
	    List<AdmissionApplication> admissions = arepo.findByEmail(request.getEmail());

	    boolean isAnyActive = admissions.stream()
	            .anyMatch(a -> a.getIsActive() != null && a.getIsActive());

	    if(!isAnyActive){
	        return "Account Inactive. Contact Admin";
	    }


	    return "Login Successfully";
	}
	
	public String resetPass(LoginDto dto) {
		if(dto.getEmail().trim().isEmpty()|| dto.getNewpass()==null||dto.getNewpass().trim().isEmpty()) {
			return "Email or Password cannot be null";
		}
		
		Optional<Student> studentemail= repo.findByEmail(dto.getEmail());
		
		if(studentemail.isEmpty()) {
			return "No Student Found For This Email";
		}
		Student student=studentemail.get();
		
		student.setPassword(encoder.encode(dto.getNewpass()));
		repo.save(student);
		return "Reset Password Succefully";
	}
	
	public List<AdmissionApplication> getStudentAdmission(String email){
		Optional<Student> studentemail=repo.findByEmail(email);
		if(studentemail.isEmpty()) {
			throw new RuntimeException("Student Not found for this Email :- "+email);
		}
	return arepo.findByEmail(email);
	}
	
	
	
	public StudentProfileDto getStudentProfileByEmail(String email) {
		
		Optional<Student> studentemail=repo.findByEmail(email);
		if(studentemail.isEmpty()) {
			System.out.println("❌ No student found for email: " + email);
			return null;
		}
		
		Student student=studentemail.get();
		
		List<AdmissionApplication> admission=arepo.findByEmail(student.getEmail());
		
		
		 List<CourseInfo> courseList = admission.stream().map(a -> {
		        // Fetch the course details from CourseRepo
		        Optional<CourseList> courseOpt = crepo.findByCourseName(a.getCourseName());

		        if (courseOpt.isPresent()) {
		            CourseList course = courseOpt.get();
		            System.out.println("✅ Found course: " + course.getCourseName());
		            return new CourseInfo(
		                course.getCourseName(),
		                course.getFree(),       // Fee
		               course.getCourseDurationMonths(),   // Duration
		                a.getCreatedAt()         // Admission date
		            );
		        } else {
		            // Course not found — return minimal info
		        	System.out.println("⚠ Course not found for: " + a.getCourseName());
		        	return new CourseInfo(
		        		    a.getCourseName(),
		        		    0.0,
		        		    0,
		        		    a.getCreatedAt()
		        		);
		        }
		    }).collect(Collectors.toList());
		
		 StudentProfileDto profile = new StudentProfileDto();
		    profile.setSname(student.getSname());
		    profile.setEmail(student.getEmail());
		    profile.setCourses(courseList);

		    System.out.println("✅ Returning profile: " + profile);
		    return profile;
		
	}

	public List<Student> getAll() {
		// TODO Auto-generated method stub
		return repo.findAll();
	}


}
