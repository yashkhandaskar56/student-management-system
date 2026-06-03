package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.PutExchange;

import com.example.dto.ExamDto;
import com.example.entity.ExamEntity;
import com.example.service.CreateExamSer;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/exam")
public class ExamController {
	
	@Autowired
	private CreateExamSer ser;
	
	 // ✅ Create Exam
	// ✅ CREATE EXAM
    @PostMapping("/create")
    public ResponseEntity<ExamEntity> createExam(
            @RequestBody ExamDto dto) {

        ExamEntity savedExam = ser.createExam(dto);
        return ResponseEntity.ok(savedExam);
    }

    // ✅ Get All Exams
    @GetMapping("/get")
    public ResponseEntity<List<ExamEntity>> getAllExams() {
        return ResponseEntity.ok(ser.getAllExam());
    }
    
    @PutMapping("/update")
    public ResponseEntity<ExamEntity> updateExam(@RequestBody ExamDto dto){
        return ResponseEntity.ok(
                ser.updateExam(dto)
        );
    }
    
//    @PutMapping("/toggle/{id}")
//    public ResponseEntity<?> toggleExam(@PathVariable Long id){
//
//        return ResponseEntity.ok(
//                ser.updateExamStatus(id)
//        );
//    }

    // ✅ Get Exam By ID
    @GetMapping("/{id}")
    public ResponseEntity<ExamEntity> getExamById(@PathVariable Long id) {
        return ResponseEntity.ok(ser.getExamById(id));
    }
    
   

    // ✅ Add Questions To Existing Exam
    @PutMapping("/{id}/questions")
    public ResponseEntity<ExamEntity> addQuestions(
            @PathVariable Long id,
            @RequestBody ExamDto dto) {

        return ResponseEntity.ok(
                ser.addQuestionToExam(id, dto)
        );
    }

    // ✅ Delete Exam
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExam(@PathVariable Long id) {
        ser.deleteExam(id);
        return ResponseEntity.ok("Exam Deleted Successfully");
    }

    // ✅ Get Exams By Student Email
    @GetMapping("/student/{email}")
    public ResponseEntity<List<ExamEntity>> getExamForStudent(
            @PathVariable String email) {

        return ResponseEntity.ok(
                ser.getExamForStudent(email)
        );
    }

    // ✅ Generate Hall Ticket PDF
    @GetMapping("/hallticket/{email}")
    public void generateHallTicket(
            @PathVariable String email,
            HttpServletResponse response) throws Exception {

        ser.generateHallticket(email, response);
    }
    
    @GetMapping("/start/{id}")
    public ResponseEntity<?> startExam(
            @PathVariable Long id,
            @RequestParam String email) {

        return ResponseEntity.ok(
                ser.startExam(id, email)
        );
    }
    
    
    
    
    
    

}
