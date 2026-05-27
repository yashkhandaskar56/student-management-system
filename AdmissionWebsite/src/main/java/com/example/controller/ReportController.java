package com.example.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.AdmissionApplication;

import com.example.repository.AdmissionRepo;
import com.example.service.ReportSer;


@RestController
@RequestMapping("api/report")
public class ReportController {
	
	@Autowired
	private ReportSer reportService;

	@GetMapping("/data")
	public Object getReport(
			@RequestParam String reportType,
			@RequestParam LocalDate from,
			@RequestParam LocalDate to
			
			){
		
		return switch (reportType) {
        case "ADMISSION" -> reportService.getadmissionReport(from, to);
        case "FEE" -> reportService.getfeeReport(from, to);
        case "EXPENSE" -> reportService.getExpenseReport(from, to);
        case "EXAM" -> reportService.getExamReport(from, to);
        case "CERTIFICATE" -> reportService.getCertificateReport(from, to);
        default -> throw new RuntimeException("Invalid Report Type");
    };
		
	}

}
