package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto {
	
	private String courseName;
	private Long availableSeat;
	private Long totalseat;
	private Integer  courseDurationMonths;
	private Long totalHours;
	private Integer dailyHours;
	
	private Double free;
	private String eligibility;
	private String description;

}
