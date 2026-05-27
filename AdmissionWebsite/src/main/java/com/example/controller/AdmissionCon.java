package com.example.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.dto.AdmissionDto;
import com.example.dto.StudentProgressDTO;
import com.example.entity.AdmissionApplication;
import com.example.repository.AdmissionRepo;
import com.example.service.AdmissionSer;
import freemarker.template.TemplateException;


@RestController
@RequestMapping("/admission")
public class AdmissionCon {
	
	@Autowired
	private AdmissionSer ser;
	
	@Autowired
	private AdmissionRepo repo;
	
	@PostMapping("/apply")
	public AdmissionApplication apply(@ModelAttribute AdmissionDto dto,
			@RequestParam(value="photo", required = false) MultipartFile photofile,
			@RequestParam(value="signature", required = false) MultipartFile signaturePhoto, 
			@RequestParam(value="adharcard", required = false) MultipartFile adharCardPhoto,
			@RequestParam(value="marksheet", required = false) MultipartFile marksheetPhoto
		)
			
			throws IOException, TemplateException {
		
		return ser.applyfrom(dto, photofile, signaturePhoto, adharCardPhoto, marksheetPhoto);
	}
	
	
	// 2️ PAY ADMISSION FORM FEE (₹500)
    @PostMapping("/pay-form-fee/{admissionId}")
    public ResponseEntity<String> payAdmissionFormFee(
            @PathVariable Long admissionId
    ) throws Exception {

        String checkoutUrl = ser.payFormFee(admissionId);

        return ResponseEntity.ok(checkoutUrl); // frontend redirect karega
    }
	
    
    //  STRIPE PAYMENT SUCCESS CALLBACK
    @GetMapping("/payment-success")
    public ResponseEntity<Void> paymentSuccess(
            @RequestParam("session_id") String sessionId
    ) throws Exception {

        ser.handleFormFeeSuccess(sessionId);

        return ResponseEntity
                .status(302)
                .header("Location", "http://localhost:8080/admissionForm.html")
                .build();
    }

    
	@GetMapping("/get")
	public List<AdmissionApplication> getAll(){
		return ser.getAllAdmission();
	}
	
	@GetMapping("/get/{id}")
	public Optional<AdmissionApplication> getStudentApllication(@PathVariable Long id){
		return ser.getStudentApplication(id);
	}
	
	@GetMapping("/get/enrollcourse")
	public ResponseEntity<List<AdmissionApplication>> getCourseAdmission(@RequestParam String email){
		return ser.getCourse(email);
	}
	
	@GetMapping("/get-All/InProgress")
	public Long getAllInProgessAdmission() {
		return ser.getAllInProgessAdmission();
	}
	
	@GetMapping("/get/courseById/{id}")
	public AdmissionApplication getCourseById(@PathVariable Long id) {

	     return ser.getCourseById(id);
	 }
	
	
	
	@GetMapping("/student/get-by-email")
	public ResponseEntity<?> getStudentByEmail(@RequestParam String email){

	    AdmissionApplication data = repo.findTopByEmailOrderByIdDesc(email);

	    if(data == null){
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Data Found");
	    }

	    return ResponseEntity.ok(data);
	}
	
	@GetMapping("/get-by-mobile")
	public ResponseEntity<?> getByMobile(@RequestParam String mobno){

	    Optional<AdmissionApplication> data = repo.findTopByMobnoOrderByIdDesc(mobno);

	    if(data.isEmpty()){
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Data Found");
	    }

	    return ResponseEntity.ok(data.get());
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateProfile(
	        @PathVariable Long id,
	        @RequestParam String sname,
	        @RequestParam String mobno,
	        @RequestParam String address,
	        @RequestParam String state,
	        @RequestParam String district,
	        @RequestParam String taluka,
	        @RequestParam String dob,
	        @RequestParam(value = "photo", required = false) MultipartFile photo
	) {

	    try {

	        Optional<AdmissionApplication> optional = repo.findById(id);

	        if(optional.isEmpty()){
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
	        }

	        AdmissionApplication student = optional.get();

	        // 🔹 Update fields
	        student.setSname(sname);
	        student.setMobno(mobno);
	        student.setAddress(address);
	        student.setState(state);
	        student.setDistrict(district);
	        student.setTaluka(taluka);
	        student.setDob(dob);

	        // 🔹 Photo upload
	        if(photo != null && !photo.isEmpty()){
	            String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
	            String uploadDir = "uploads/";

	            File dir = new File(uploadDir);
	            if(!dir.exists()) dir.mkdirs();

	            File file = new File(uploadDir + fileName);
	            photo.transferTo(file);

	            student.setPhotopath(fileName);
	        }

	        repo.save(student);

	        return ResponseEntity.ok("Profile Updated Successfully");

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Update Failed");
	    }
	}
	
	@PutMapping("/toggle-status/{id}")
	public ResponseEntity<?> toggleStatus(@PathVariable Long id){

	    Optional<AdmissionApplication> optional = repo.findById(id);

	    if(optional.isEmpty()){
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
	    }

	    AdmissionApplication student = optional.get();

	    // 🔥 toggle logic
	    if(student.getIsActive() == null){
	        student.setIsActive(true);
	    } else {
	        student.setIsActive(!student.getIsActive());
	    }

	    repo.save(student);

	    return ResponseEntity.ok(student.getIsActive() ? "Activated" : "Deactivated");
	}
	
	@GetMapping("/check-status")
	public ResponseEntity<?> checkStatus(@RequestParam String email){

	    List<AdmissionApplication> list = repo.findByEmail(email);

	    boolean isActive = list.stream()
	            .anyMatch(a -> a.getIsActive() != null && a.getIsActive());

	    return ResponseEntity.ok(isActive);
	}
	
	@GetMapping("/students-progress")
	public List<StudentProgressDTO> getStudentsProgress(){

	    return ser.getStudentsWithProgress();
	}
	
	@GetMapping("/student-progress/student")
	public List<StudentProgressDTO> getStudentProgress(
	        @RequestParam String email){

	    return ser.getStudentProgressByEmail(email);
	}
	
	@PostMapping("/import")
	public ResponseEntity<?> importStudents(
	        @RequestParam("file") MultipartFile file) {

	    try {

	        String result =
	                ser.importExcel(file);

	        return ResponseEntity.ok(result);

	    } catch (Exception e) {

	        e.printStackTrace();

	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Import Failed");
	    }
	}
	
	
	

}
