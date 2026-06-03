package com.example.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.AdmissionApplication;
import com.example.entity.CertificateEntity;
import com.example.repository.AdmissionRepo;
import com.example.service.CertificateSer;

@RestController
@RequestMapping("/api/student/certificate")
@CrossOrigin
public class CertificateCon {
	
	@Autowired
	private CertificateSer ser;
	
	@Autowired
	private AdmissionRepo arepo;
	
	
	 // =========================
    // 1️⃣ Show Certificate
    // =========================
    @GetMapping("/view")
    public CertificateEntity viewCertificate(
            @RequestParam String email,
            @RequestParam String studentName,
            @RequestParam String courseName){

        return ser
                .viewCertificate(email, studentName, courseName);
    }
    
    @GetMapping("/get")
    public List<CertificateEntity> getAll(){
    	return ser.getAllCerti();
    }
    
    @GetMapping("/getByEmail")
    public List<CertificateEntity> getByEmail(@RequestParam String email){
        return ser.getByEmail(email);
    }
    
    // =========================
    // 2️⃣ Download PDF
    // =========================
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadCertificate(
            @RequestParam String email,
            @RequestParam String courseName){

        byte[] pdf = ser.downloadCertificatePdf(email, courseName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=certificate.pdf") // ✅ CHANGE
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
    
    @GetMapping("/viewPdf")
    public ResponseEntity<byte[]> viewPdf(
            @RequestParam String email,
            @RequestParam String courseName){

        byte[] pdf = ser.downloadCertificatePdf(email, courseName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=certificate.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
    
    @GetMapping("/details")
    public Map<String, Object> getStudentDetails(@RequestParam String email){

        Map<String, Object> map = new HashMap<>();

        // student name
        AdmissionApplication admission = arepo.findTopByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        map.put("name", admission.getSname());

        // student ke courses
        List<AdmissionApplication> admissions = arepo.findByEmail(email);

        List<Map<String, String>> courses = admissions.stream()
                .map(a -> {
                    Map<String, String> c = new HashMap<>();
                    c.put("courseName", a.getCourseName());
                    return c;
                })
                .toList();

        map.put("courses", courses);

        return map;
    }
    
 // ===============================
 // VERIFY CERTIFICATE
 // ===============================

 @GetMapping("/verify")
 public ResponseEntity<?> verifyCertificate(
         @RequestParam String certificateNo){

     try {

         CertificateEntity certificate =
                 ser.verifyCertificate(certificateNo);

         return ResponseEntity.ok(certificate);

     } catch (Exception e){

         Map<String,String> error = new HashMap<>();

         error.put("message", e.getMessage());

         return ResponseEntity.badRequest().body(error);
     }
 }


}
