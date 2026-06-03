package com.example.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionDto {
	
	private Long examId;
    private String email;
    private List<SubmissionAnswerDto> answers;
	

}
