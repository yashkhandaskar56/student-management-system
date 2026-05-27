package com.example.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.StudentFreeDto;
import com.example.entity.AdmissionApplication;
import com.example.entity.StudentFee;
import com.example.repository.AdmissionRepo;
import com.example.service.StudentFeeService;

@RestController
@RequestMapping("/Student-free")
public class StudentFeeController {
	
	@Autowired
	private StudentFeeService ser;
	
	@Autowired
	private AdmissionRepo arepo;
	
	@PutMapping("/add")
    public Map<String, Object> addPayment(@RequestBody StudentFreeDto dto) throws Exception {
        return ser.processPayment(dto);
    }
	
	@GetMapping("/get")
	public List<StudentFee> getDetails(){
		return ser.getAll();
	}

	@GetMapping("/free-details")
	public AdmissionApplication getStudentFreeDetails(@RequestParam String email,
			@RequestParam String courseName) {
		
		return ser.getStudentFeeDetails(email, courseName);	
	}
	
	 @GetMapping("/verify-payment/{sessionId}")
	    public Map<String, Object> verifyPayment(@PathVariable String sessionId) throws Exception {
	        return ser.verifyStripePayment(sessionId);
	    }
	
	@GetMapping("/fee-history")
	public List<StudentFee> getPaymentHistory(@RequestParam String email) {
	    return ser.getPaymentHistory(email);
	}
	
	@GetMapping("/pending")
	public List<StudentFee> getPendingFees(){
	    return ser.getPendingFees();
	}
	
	
	
	@GetMapping("/free-details/total")
	public Long getTotalFee() {
		return ser.getTotalFee();
	}
	
	@GetMapping("/free-details/paid")
	public Long getTotalPaid() {
		return ser.getPaidFee();
	}
	
	@GetMapping("/free-details/remening")
	public Long getTotalRemining() {
		return ser.getRemeningFee();
	}
	
	@PostMapping("/send-feereminder")
	public ResponseEntity<?> sendReminderByEmail(@RequestParam String email) {

	    List<AdmissionApplication> admissionOpt = arepo.findByEmail(email);

	    if (admissionOpt.isEmpty()) {
	        return ResponseEntity.badRequest().body(Map.of("message", "No student found with this email"));
	    }

	    AdmissionApplication admission = admissionOpt.get(0);

	    try {
	        ser.sendReminderEmail(
	                admission.getEmail(),
	                admission.getSname(),
	                admission.getTotalfree(),
	                admission.getPaidfree()
	        );
	        return ResponseEntity.ok(Map.of("message", "Fee Reminder Email Sent Successfully"));
	    } catch (Exception e) {
	        return ResponseEntity.status(500).body(Map.of("message", "Error sending email: " + e.getMessage()));
	    }
	}
	
	@GetMapping("/payment-details/{sessionId}")
	public ResponseEntity<?> getPaymentDetails(@PathVariable String sessionId) {
	    StudentFee fee = ser.getPaymentHistoryBySessionId(sessionId);
	    if (fee == null) {
	        return ResponseEntity.badRequest().body(Map.of("message", "Payment not found"));
	    }
	    AdmissionApplication admission = arepo.findByEmailAndCourseName(fee.getEmail(), fee.getCourseName())
	            .orElseThrow(() -> new RuntimeException("Student not found"));

	    Map<String, Object> response = new HashMap<>();
	    response.put("name", admission.getSname());
	    response.put("email", admission.getEmail());
	    response.put("courseName", admission.getCourseName());
	    response.put("totalFee", fee.getTotalfee());
	    response.put("paidAmount", fee.getPaidfee());
	    response.put("remainingAmount", fee.getRemaningfee());

	    return ResponseEntity.ok(response);
	}
	
	@PostMapping("/offline-payment")
	public String offlinePaymentProcess(@RequestBody StudentFreeDto dto) throws Exception {
		return ser.offlinePaymentProcess(dto);
	}
	
	@GetMapping("/by-course")
	public List<StudentFee> getByCourse(@RequestParam String courseName){
	    return ser.getByCourseName(courseName);
	}
	
	@GetMapping("/summary")
	public Map<String, Object> getPaymentSummary(@RequestParam String email){

	    List<StudentFee> list = ser.getPaymentHistory(email);

	    double total = 0;
	    double paid = 0;
	    double remaining = 0;

	    for(StudentFee f : list){
	        total += f.getTotalfee();
	        paid += f.getPaidfee();
	        remaining += f.getRemaningfee();
	    }

	    Map<String, Object> map = new HashMap<>();
	    map.put("total", total);
	    map.put("paid", paid);
	    map.put("remaining", remaining);

	    return map;
	}
	
	@GetMapping("/course-payment")
	public List<StudentFee> getCoursePayment(@RequestParam String email){
	    return ser.getPaymentHistoryFee(email);
	}

}
