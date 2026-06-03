package com.example.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public  class CourseInfo {

	private String courseName;
    private Double fee;
    private Integer courseDurationMonths;
    private LocalDate datetime;
}
