package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamSummaryDto {
	
	private Long totalExams;
    private Long attempted;
    private Long passed;
    private Long failed;

}
