package com.example.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dto.AdmissionReportDto;
import com.example.dto.ExpenseReportDto;
import com.example.dto.FeeReportDto;
import com.example.entity.AdmissionApplication;
import com.example.entity.CertificateEntity;
import com.example.entity.ExpensesEntity;
import com.example.entity.StudentFee;
import com.example.entity.SubmissionExam;
import com.example.repository.AdmissionRepo;
import com.example.repository.CertificateRepo;
import com.example.repository.ExpenseRepo;
import com.example.repository.StudentFeeRepo;
import com.example.repository.SubmissionRepo;

@Service
public class ReportSer {
	
	@Autowired
	private AdmissionRepo admissionrepo;
	@Autowired
	private StudentFeeRepo feeRepo;
	@Autowired
	private SubmissionRepo submitRepo;
	@Autowired
	private ExpenseRepo expenseRepo;
	@Autowired
	private CertificateRepo certificateRepo;
	
	public List<AdmissionReportDto> getadmissionReport(LocalDate from,LocalDate to){
		return admissionrepo.findAdmissionReport(from,to);
	}
	
	public List<StudentFee> getfeeReport(LocalDate from,LocalDate to){
		return feeRepo.findBypaymentdateBetween(from,to);
	}
	
	public List<ExpensesEntity> getExpenseReport(LocalDate from,LocalDate to){
		return expenseRepo.findByExpenseDateBetween(from,to);
	}
	
	public List<SubmissionExam> getExamReport(LocalDate from, LocalDate to){

	    LocalDateTime start = from.atStartOfDay();      // 00:00
	    LocalDateTime end = to.atTime(LocalTime.MAX);   // 23:59

	    return submitRepo.findExamReport(start, end);
	}
	
	public List<CertificateEntity> getCertificateReport(LocalDate from, LocalDate to){
	    return certificateRepo.findByIssuedateBetween(from, to);
	}

}
