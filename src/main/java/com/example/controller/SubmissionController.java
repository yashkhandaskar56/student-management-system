package com.example.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.ExamAttemptDto;
import com.example.dto.ExamSummaryDto;
import com.example.dto.ScoreCardDto;
import com.example.dto.SubmissionDto;
import com.example.entity.SubmissionExam;
import com.example.repository.SubmissionRepo;
import com.example.service.SubmissionService;

import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

@RestController
@RequestMapping("/submission")
@CrossOrigin(origins = "*")

public class SubmissionController {
	
	@Autowired
	private SubmissionService ser;
	
	@Autowired
	private SubmissionRepo repo;
	
	// ==========================
    // SUBMIT EXAM
    // ==========================
    @PostMapping("/submit")
    public ResponseEntity<SubmissionExam> submitExam(
            @RequestBody SubmissionDto dto) throws Exception {

        return ResponseEntity.ok(
                ser.submitExam(dto)
        );
    }

    // ==========================
    // GET ALL SUBMISSIONS
    // ==========================
    @GetMapping("/get")
    public ResponseEntity<List<SubmissionExam>> getAll() {
        return ResponseEntity.ok(
                ser.getAll()
        );
    }

    // ==========================
    // GET SCORE CARD
    // ==========================
    @GetMapping("/result/{submissionId}")
    public ResponseEntity<ScoreCardDto> getScoreCard(
            @PathVariable Long submissionId) {

        return ResponseEntity.ok(
                ser.getScoreCard(submissionId)
        );
    }

    // ==========================
    // ADMIN DASHBOARD SUMMARY
    // ==========================
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getAdminSummary() {

        Map<String, Object> summary = new HashMap<>();

        summary.put("totalExams", ser.countExam());
        summary.put("totalAttempts", ser.countExam());
        summary.put("attemptsToday", ser.attemptToday());
        summary.put("passingPercentage", ser.getPassingPercentage());
        summary.put("failedPercentage", ser.getFailedPercentage());

        return ResponseEntity.ok(summary);
    }

    // ==========================
    // STUDENT SUMMARY
    // ==========================
    @GetMapping("/student/{email}/summary")
    public ResponseEntity<ExamSummaryDto> getStudentSummary(
            @PathVariable String email) {

        return ResponseEntity.ok(
                ser.getSummary(email)
        );
    }

    // ==========================
    // STUDENT HISTORY
    // ==========================
    @GetMapping("/student/{email}/history")
    public ResponseEntity<List<ExamAttemptDto>> getStudentHistory(
            @PathVariable String email) {

        return ResponseEntity.ok(
                ser.getStudentExamHistory(email)
        );
    }

    // ==========================
    // CHECK PASS STATUS
    // ==========================
    @GetMapping("/student/{email}/pass-status")
    public ResponseEntity<Boolean> isExamPassed(
            @PathVariable String email,
            @RequestParam String courseName) {

        return ResponseEntity.ok(
                ser.isExamPassed(email, courseName)
        );
    }
    
    @GetMapping("/check-attempt")
    public boolean checkAttempt(@RequestParam String email,
                                @RequestParam Long examId) {

        return repo.existsByEmailAndExam_ExamId(email, examId);
    }

}
