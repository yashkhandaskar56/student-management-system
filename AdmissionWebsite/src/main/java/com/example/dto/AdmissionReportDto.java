package com.example.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdmissionReportDto {
	
	private Long id;
    private String sname;
    private String email;
    private String courseName;
    private LocalDate createdAt;

}
