package com.example.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlySummaryReportDto {
	
	private LocalDate fromDate;
    private LocalDate toDate;

    private long totalAdmissions;
    private long totalCourse;

    private double totalFeePaid;
    private double totalRemainingFee;

    private double totalExpense;
    private double netBalance;

}
