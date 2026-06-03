package com.example.dto;

import java.time.LocalDate;

import com.example.entity.ExpenseType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDto {

	 private ExpenseType expenseType;

	    private String title;

	    private String description;

	    private Double amount;

	    private String paidTo;

	    private String paymentMethod;

	    private String billNumber;

	    private LocalDate expenseDate;
}
