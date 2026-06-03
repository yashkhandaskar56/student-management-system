package com.example.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.MonthlySummaryDTO;
import com.example.dto.MonthlySummaryReportDto;
import com.example.dto.StudentDetailsDTO;
import com.example.dto.adminCard;
import com.example.entity.AdmissionApplication;
import com.example.entity.CourseList;
import com.example.entity.Student;
import com.example.repository.AdmissionRepo;
import com.example.repository.CourseRepo;
import com.example.service.AdminDashboardSer;

@RestController
@RequestMapping("/Admin-dashboard")
public class AdminDashboardController {
	
	@Autowired
	private AdmissionRepo admissionRepo;
	
	@Autowired
	private AdminDashboardSer ser;
	
	@Autowired
	private CourseRepo courseRepo;
	
	@GetMapping("/getStudent")
	public List<Student> getAllStudent(){
		return ser.getAllStudentList();
	}
	
	@GetMapping("/getAdmission")
		public List<AdmissionApplication> getAllAdmission(){
			return ser.getAllAdmission();
	}
	
	@GetMapping("/card")
	   public adminCard getStats(){
			return ser.getCard();
	}
	
	@GetMapping("/recent-admissions")
	  public List<AdmissionApplication> recentAdmissions(){
	       return ser.getRecentAdmissions();
	}
	
	@GetMapping("/course-popularity")
	public List<Map<String,Object>> getCoursePopularity(){
	    return ser.getCoursePopularity();
	}
	
	@GetMapping("/monthly-income/{year}")
	public List<MonthlySummaryDTO> getMonthlyIncome(@PathVariable int year){
		return ser.getMonthlySummary(year);
	}
	
	@GetMapping("/monthly-expense/{year}")
	public List<MonthlySummaryDTO> getMonthlyExpense(@PathVariable int year){
		return ser.getMonthyExpense(year);
	}
	
	@GetMapping("/monthly-IncomeExpense/{year}")
	public List<Map<String, Object>> getIncomeExpenseSummary(@PathVariable int year){
		return ser.getIncomeExpenseSummary(year);
	}
	
	@GetMapping("/Monthly-Report")
	public MonthlySummaryReportDto getReport(@RequestParam LocalDate fromDate,@RequestParam LocalDate toDate) {
		return ser.getreport(fromDate, toDate);
	}
	
	@GetMapping("/student-full-details/{id}")
	public StudentDetailsDTO getStudentFullDetails(
	        @PathVariable Long id){

	    AdmissionApplication s =
	        admissionRepo.findById(id)
	        .orElseThrow();

	    Optional<CourseList> course =
	        courseRepo.findByCourseName(
	            s.getCourseName()
	        );

	    StudentDetailsDTO dto =
	        new StudentDetailsDTO();

	    dto.setId(s.getId());

	    dto.setSname(s.getSname());

	    dto.setEmail(s.getEmail());

	    dto.setMobno(s.getMobno());

	    dto.setCourseName(s.getCourseName());

	    dto.setAddress(s.getAddress());

	    dto.setState(s.getState());

	    dto.setDistrict(s.getDistrict());

	    dto.setTaluka(s.getTaluka());

	    dto.setDob(s.getDob());

	    dto.setGender(s.getGender());

	    dto.setPhotopath(s.getPhotopath());

	    dto.setSignaturePhoto(
	        s.getSignaturePhoto()
	    );

	    dto.setAdharCradPhoto(
	        s.getAdharCradPhoto()
	    );

	    dto.setMarksheet(
	        s.getMarksheet()
	    );

	    dto.setTotalfree(
	        s.getTotalfree()
	    );

	    dto.setPaidfree(
	        s.getPaidfree()
	    );

	    dto.setRemainingfree(
	        s.getRemainingfree()
	    );

	    dto.setCourseStatus(
	        s.getCourseStatus().name()
	    );

	    dto.setIsActive(
	        s.getIsActive()
	    );

	    dto.setCreatedAt(
	        s.getCreatedAt().toString()
	    );

	    // HOURS LOGIC

	    int totalHours = 0;

	    int courseDuration = 0;

	    if(course.isPresent()){

	        CourseList c = course.get();

	        if(c.getTotalHours() != null){
	            totalHours = c.getTotalHours().intValue();
	        }

	        if(c.getCourseDurationMonths() != null){
	            courseDuration =
	                c.getCourseDurationMonths();
	        }
	    }
	    
	    dto.setCourseDuration(courseDuration);

	 // ================= HOURS CALCULATION =================

	    int dailyHours = 0;

	    if(course.isPresent()){

	        CourseList c = course.get();

	        // TOTAL HOURS
	        if(c.getTotalHours() != null){
	            totalHours = c.getTotalHours().intValue();
	        }

	        // DAILY HOURS
	        if(c.getDailyHours() != null){
	            dailyHours = c.getDailyHours();
	        }

	        // COURSE DURATION
	        if(c.getCourseDurationMonths() != null){
	            courseDuration = c.getCourseDurationMonths();
	        }
	    }

	    dto.setCourseDuration(courseDuration);

	    // ================= DAYS PASSED =================

	    long daysPassed = 0;

	    if(s.getCreatedAt() != null){

	        daysPassed =
	            java.time.temporal.ChronoUnit.DAYS
	            .between(s.getCreatedAt(), LocalDate.now());

	        if(daysPassed < 0){
	            daysPassed = 0;
	        }
	    }

	    // ================= COMPLETED HOURS =================

	    long completedHours =
	        daysPassed * dailyHours;

	    // TOTAL HOURS CROSS NAHI HONA CHAHIYE

	    if(completedHours > totalHours){
	        completedHours = totalHours;
	    }

	    // ================= REMAINING HOURS =================

	    Integer remainingHours =
	        (int) (totalHours - completedHours);

	    if(remainingHours < 0){
	        remainingHours = 0;
	    }

	    // ================= PROGRESS =================

	    double progress = 0;

	    if(totalHours > 0){

	        progress =
	            (completedHours * 100.0)
	            / totalHours;
	    }

	    // ================= SET DTO =================

	    dto.setTotalHours(totalHours);

	    dto.setCompletedHours((int) completedHours);

	    dto.setRemainingHours(remainingHours);

	    dto.setProgress(progress);
	    return dto;
	}
	
}


