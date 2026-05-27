package com.example.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dto.MonthlySummaryDTO;
import com.example.dto.MonthlySummaryReportDto;
import com.example.dto.adminCard;
import com.example.entity.AdmissionApplication;
import com.example.entity.Student;
import com.example.repository.AdmissionRepo;
import com.example.repository.CertificateRepo;
import com.example.repository.CourseRepo;
import com.example.repository.ExamRepo;
import com.example.repository.ExpenseRepo;
import com.example.repository.StudentRepo;

@Service
public class AdminDashboardSer {
	
	@Autowired
	private StudentRepo studentRepo;
	
	@Autowired
	private AdmissionRepo admissionRepo;
	
	@Autowired
	private ExpenseRepo expenseRepo;
	
	@Autowired
	private CourseRepo courseRepo;
	
	@Autowired
	private ExamRepo examRepo;
	
	@Autowired
	private CertificateRepo certiRepo;
	
	
	public List<Student> getAllStudentList(){
		return studentRepo.findAll();
	}
	
	public List<AdmissionApplication> getAllAdmission(){
		return admissionRepo.findAll();
	}
	
	private static final String[] MONTH_NAMES =
        {"January","February","March","April","May","June",
         "July","August","September","October","November","December"};
	
	// 🔹 Monthly Income
	public List<MonthlySummaryDTO> getMonthlySummary(int year){
		List<Object[]> rows=admissionRepo.getMonthlyIncome(year);
		List<MonthlySummaryDTO> list=new ArrayList<>();
		
		for(Object[] row:rows) {
			int months=(int) row[0];
			double total=row[1] != null ? ((Number)row[1]).doubleValue():0;
			
			list.add(new MonthlySummaryDTO(MONTH_NAMES[months-1],total));
		}
		return list;
		
	}


    // 🔹 Monthly Expense by Year
	public List<MonthlySummaryDTO> getMonthyExpense(int year) {
		// TODO Auto-generated method stub
		
		List<Object[]> rows=expenseRepo.getMonthlyExpense(year);
		List<MonthlySummaryDTO> list=new ArrayList<>();
		
		for(Object[] row:rows) {
			int months=(int) row[0];
			double total=row[1] !=null ?((Number) row[1]).doubleValue():0;
			
			list.add(new MonthlySummaryDTO(MONTH_NAMES[months-1], total));
		}
		return list;
	}
	
	
	// 🔹 Combined Summary (Income + Expense + Profit/Loss)
	public List<Map<String , Object>> getIncomeExpenseSummary(int year){
		
		Map<String, Object> incomemap=new HashMap<>();
		Map<String, Object> expensemap=new HashMap<>();
		
		for(MonthlySummaryDTO dto:getMonthlySummary(year)) {
			incomemap.put(dto.getMonth(), dto.getTotal());
		}
		
		for(MonthlySummaryDTO dto:getMonthyExpense(year)) {
			expensemap.put(dto.getMonth(), dto.getTotal());
		}
		
		List<Map<String, Object>> list=new ArrayList<>();
		
		for( String month:MONTH_NAMES) {
			double income=(double) incomemap.getOrDefault(month, 0.0);
			double expense=(double) expensemap.getOrDefault(month, 0.0);
			
			Map<String, Object> map=new HashMap<>();
			
			map.put("Month", month);
			map.put("Income", income);
			map.put("Expense", expense);
			map.put("Profit", income-expense);
			
			list.add(map);
		}
		return list;
	}
	
	public MonthlySummaryReportDto getreport(LocalDate fromDate, LocalDate toDate) {

	    MonthlySummaryReportDto dto = new MonthlySummaryReportDto();
	    dto.setToDate(fromDate);
	    dto.setFromDate(toDate);

	    // 🔹 Convert month/year to date range
	    LocalDateTime startDate = fromDate.atStartOfDay();
	    LocalDateTime endDate = toDate.plusDays(1).atStartOfDay();

	    // 🔹 Call updated repository methods
	    long student = admissionRepo.countAdmissionsBetween(startDate, endDate);
	    long course = courseRepo.gettotalCourses();
	    double totalPaidFee = admissionRepo.totalPaidFeeBetween(startDate, endDate);
	    double remainingFee = admissionRepo.getTotalRemainingFee(); // total remaining overall
	    double totalExpense = expenseRepo.totalExpenseBetween(startDate, endDate);

	    dto.setTotalAdmissions(student);
	    dto.setTotalCourse(course);
	    dto.setTotalFeePaid(totalPaidFee);
	    dto.setTotalRemainingFee(remainingFee);
	    dto.setTotalExpense(totalExpense);
	    dto.setNetBalance(totalPaidFee - totalExpense);

	    return dto;
	}
	
	public adminCard getCard() {
		adminCard card=new adminCard();
		card.setTotalStudents(studentRepo.count());
		card.setCurrentAdmissions(admissionRepo.gettotalAdmission());
		card.setTotalCourses(courseRepo.count());
		card.setTotalExams(examRepo.count());
		
		card.setPendingFees(admissionRepo.getTotalRemainingFee()==null?0:admissionRepo.getTotalRemainingFee());
		card.setMonthlyIncome(admissionRepo.getMonthlyIncome()==null?0:admissionRepo.getMonthlyIncome());
		card.setMonthlyExpense(expenseRepo.getMonthlyExpense()==null?0:expenseRepo.getMonthlyExpense());
		
		card.setTotalcertificate(certiRepo.count());
		
		return card;
	}
	
	public List<AdmissionApplication> getRecentAdmissions(){
	    return admissionRepo.getRecentAdmissions();
	    
	}
	
	public List<Map<String,Object>> getCoursePopularity(){

	    List<Object[]> list =
	    		admissionRepo.getCoursePopularity();

	    List<Map<String,Object>> result =
	            new ArrayList<>();

	    for(Object[] obj : list){

	        Map<String,Object> map =
	                new HashMap<>();

	        map.put("courseName", obj[0]);

	        map.put("students", obj[1]);

	        result.add(map);
	    }

	    return result;
	}

}
