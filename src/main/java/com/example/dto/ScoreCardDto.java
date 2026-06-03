package com.example.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreCardDto {
	
	private String name;
	private String email;
	private String examName;
	
	private Integer totalMarks;
	private Integer totalScore;
	
	private Double percentage;
	private String grade;
	
	private LocalDateTime submittedAt;
	
	

}
