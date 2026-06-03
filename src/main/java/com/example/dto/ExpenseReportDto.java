package com.example.dto;

import java.time.LocalDate;

import com.example.entity.ExpenseType;

import lombok.Data;

@Data
public class ExpenseReportDto {
	
	private Long id;
	private ExpenseType expenseType;
	private String description;
	private Double amount;
	private String paidTo;
	private String billNumber;
	private LocalDate expenseDate=LocalDate.now();

}
