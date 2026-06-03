package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.ExamAssign;
import com.example.entity.ExamEntity;
import com.example.repository.ExamAssignRepo;
import com.example.repository.ExamRepo;

@Service
public class ExamAssignSer {
	
	@Autowired
	private ExamAssignRepo assignRepo;
	
	@Autowired
	private ExamRepo examRepo;
	
	public String assignExamToStudent(Long examId, String email) {

	    if(email == null || email.isEmpty()){
	        throw new RuntimeException("Email is required");
	    }

	    boolean exists = assignRepo
	        .existsByStudentEmailIgnoreCaseAndExam_ExamId(email, examId);

	    if (exists) {
	        return "Already Assigned";
	    }

	    ExamEntity exam = examRepo.findById(examId)
	            .orElseThrow(() -> new RuntimeException("Exam not found"));

	    ExamAssign se = new ExamAssign();
	    se.setExam(exam);
	    se.setStudentEmail(email.toLowerCase());
	    se.setAvailable(true);

	    assignRepo.save(se);

	    return "Assigned Successfully";
	}
	
	

}
