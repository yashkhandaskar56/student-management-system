package com.example.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class FeeReportDto {
	
	private Long id;
	private String email;
	private String courseName;
	
	private Double totalfee;
	private Double paidfee;
	private Double remaningfee;
	
	private LocalDate paymentdate;

}
