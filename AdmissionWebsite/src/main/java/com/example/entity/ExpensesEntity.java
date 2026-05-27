package com.example.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpensesEntity {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    // Expense Category
	    @Enumerated(EnumType.STRING)
	    private ExpenseType expenseType;

	    // Short Title
	    private String title;

	    // Full Description
	    @Column(length = 1000)
	    private String description;

	    // Expense Amount
	    private Double amount;

	    // Paid To
	    private String paidTo;

	    // Payment Method
	    private String paymentMethod;

	    // Bill Number
	    private String billNumber;

	    // Receipt File Name
	    private String receiptFile;

	    // Expense Date
	    private LocalDate expenseDate;

	    // Created By
	    private String createdBy;

	    // Created Time
	    private LocalDateTime createdAt;


}
