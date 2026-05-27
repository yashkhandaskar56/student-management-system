package com.example.dto;

import java.time.LocalDate;

import com.example.entity.CourseStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentProgressDTO {

	 private Long id;
	    private String sname;
	    private String email;
	    private String mobno;
	    private String courseName;

	    private Long totalHours;
	    private Integer completedHours;
	    private Long remainingHours;

	    private LocalDate createdAt;
	    private Double progress;

	    private CourseStatus courseStatus;
	    private Boolean isActive;
}
