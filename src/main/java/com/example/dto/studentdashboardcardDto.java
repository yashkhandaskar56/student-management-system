package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class studentdashboardcardDto {
	
	
	 private long totalCourses;
	 private long completedCourses;
	 private long examAttempts;
	 private long certificates;
	 private double totalFee;
	 private double pendingFee;
	 private double paidFee;
	 private int totalHours;

}
