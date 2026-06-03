package com.example.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table
@AllArgsConstructor
@NoArgsConstructor
public class StudentFee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String email;
	private String courseName;
	
	private Double totalfee;
	private Double paidfee;
	private Double remaningfee;
	
	private LocalDate paymentdate;
	private String paymentIntentId;

}
