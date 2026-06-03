package com.example.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamAttemptDto {
	
	    private Long submissionId;
	    private String examName;
	    private int score;
	    private int totalMarks;
	    private double percentage;
	    private String grade;
	    private LocalDateTime submittedAt;

}
