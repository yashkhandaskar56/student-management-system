package com.example.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.example.service.CreateExamSer;
import com.example.service.SubmissionService;
import com.itextpdf.text.DocumentException;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.dto.ExamAttemptDto;
import com.example.dto.ExamSummaryDto;
import com.example.dto.CourseProgressDTO;
import com.example.dto.studentdashboardcardDto;
import com.example.entity.AdmissionApplication;
import com.example.entity.CourseList;
import com.example.entity.CourseMaterial;
import com.example.entity.CourseStatus;
import com.example.entity.ExamEntity;
import com.example.repository.AdmissionRepo;
import com.example.repository.CertificateRepo;
import com.example.repository.CourseRepo;
import com.example.repository.SubmissionRepo;
import com.example.service.AdmissionSer;
import com.example.service.CourseMaterialSer;
import com.example.service.CourseSer;

@RestController
@RequestMapping("/api/student")
@CrossOrigin("*")
public class StudentDashboard {

	@Autowired
	private CourseMaterialSer cser;

	@Autowired
	private AdmissionSer aser;
    
    @Autowired
    private AdmissionRepo arepo;
    @Autowired 
    private SubmissionRepo srepo;
    @Autowired
    private CertificateRepo crepo;
    
    @Autowired
	private CreateExamSer examser ;
    
    @Autowired
    private SubmissionRepo submissionRepo;
    
    @Autowired
    private SubmissionService submissionser;
    @Autowired
    private CourseRepo crepository;

    
    @GetMapping("/check")
    public Map<String, Object> checkStudent(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();

        List<AdmissionApplication> student = arepo.findByEmail(email);
        
        response.put("exists", !student.isEmpty());
        return response;
    }

    // Get student details
    @GetMapping("/details/{email}")
    public ResponseEntity<?> getStudentDetails(@PathVariable String email) {

        List<AdmissionApplication> students = arepo.findByEmail(email);

        if (students.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Student not found!");
            return ResponseEntity.status(404).body(error);
        }

        // Return all course applications for that email
        return ResponseEntity.ok(students);
    }
    
    @GetMapping("/get-courseName")
	public ResponseEntity<?> getMaterialByCourseName(@RequestParam String courseName){
		List<CourseMaterial> list=cser.getByCourseName(courseName);
		if(list == null||list.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("List Not Found");
		}
		
		return ResponseEntity.ok(list);
	}
    
    @GetMapping("/download/id/{id}")
	public ResponseEntity<?> downloadById(@PathVariable Long id) throws FileNotFoundException{
		
		CourseMaterial m=cser.getById(id);
		
		String uploadDir = "C:\\Users\\DELL\\OneDrive\\Desktop\\CourseMaterial\\";
		File file=new File(uploadDir+m.getFilename());
		System.out.println("DEBUG: Trying file = " + file);
		
		if(!file.exists()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File Not Found");
			
		}
		
		InputStreamResource resource=new InputStreamResource(new FileInputStream(file));
		return ResponseEntity.ok().contentLength(file.length())
				.header(HttpHeaders.CONTENT_DISPOSITION, 
						"attachment; fileName=\""+m.getFilename()+"\"")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(resource);
		
	}
    

 // Get total exam attempts for a student
    @GetMapping("/exam/attempts")
    public Map<String, Object> getTotalExamAttempts(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();

        Long totalAttempts = submissionRepo.CountByExamStudentEmail(email);
        response.put("examAttempts", totalAttempts);

        return response;
    }
    
    @GetMapping("/exam")
    public List<ExamEntity> getExamForStudent(@RequestParam String email){
    	return examser.getExamForStudent(email);
    }
    
    
    @GetMapping("/exam-summary")
    public ExamSummaryDto getExamSummary(@RequestParam String email) {
    	return submissionser.getSummary(email);
    }
    
    @GetMapping("/exam-history")
	public List<ExamAttemptDto> getHistory(@RequestParam String email){
	    return submissionser.getStudentExamHistory(email);
	}
    
    @GetMapping("/hall-ticket")
    public void downloadHallTicket(
            @RequestParam String email,
            HttpServletResponse response) throws DocumentException, IOException {

    	examser.generateHallticket(email, response);
    }
    
    @GetMapping("/card")
    public studentdashboardcardDto getDashboard(@RequestParam String email) {

        System.out.println("Email from request: [" + email + "]");

        long totalCourses = arepo.countByEmail(email);
        long completedCourses = arepo.countCompletedCourses(email);
        long examAttempts = srepo.countExamAttempts(email);
        long certificates = crepo.countCertificates(email);

        // ✅ GET ALL COURSES BY EMAIL
        List<AdmissionApplication> list = arepo.findByEmail(email);

        double totalFee = 0;
        double pendingFee = 0;
        double paidFee=0;
        long totalHours = 0;

        // ✅ CALCULATE FEES
        for(AdmissionApplication a : list){

            totalFee += a.getTotalfree() != null
                    ? a.getTotalfree()
                    : 0;

            pendingFee +=
                    (a.getTotalfree() - a.getPaidfree());

            paidFee += a.getPaidfree() != null
                    ? a.getPaidfree()
                    : 0;

            // =========================
            // TOTAL HOURS CALCULATION
            // =========================

            Optional<CourseList> courseOptional =
                    crepository.findByCourseName(a.getCourseName());

            if(courseOptional.isPresent()){

                CourseList course = courseOptional.get();

                if(course.getTotalHours() != null){

                    totalHours += course.getTotalHours();

                }
            }
        }

        // ✅ RETURN DTO WITH ALL DATA
        return new studentdashboardcardDto(
                totalCourses,
                completedCourses,
                examAttempts,
                certificates,
                totalFee,
                pendingFee,
                paidFee,
                (int) totalHours
        );
    }
    
    @GetMapping("/course-progress/{email}")
    public List<CourseProgressDTO> getProgress(@PathVariable String email) {
        return aser.getCourseProgress(email);
    }
    
}
