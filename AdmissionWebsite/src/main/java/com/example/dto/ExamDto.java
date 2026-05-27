package com.example.dto;

import java.util.List;

import com.example.entity.QuestionEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamDto {

	private Long examId;
	private String title;
	private Long durationMinutes;
	private String courseName;
	
	@JsonProperty("questionList")
	private List<QuestionDto> questionList;
	
	
}
