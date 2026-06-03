package com.example.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.CertificateEntity;
import com.example.pdf.CertificatePdfGenerator;
import com.example.repository.AdmissionRepo;
import com.example.repository.CertificateRepo;

@Service
public class CertificateSer {

    @Autowired
    private CertificateRepo cerRepo;

    @Autowired
    private CourseSer courseSer;

    @Autowired
    private StudentFeeService feeSer;

    @Autowired
    private SubmissionService submissionSer;

    @Autowired
    private AdmissionRepo admissionRepo;

    // ===============================
    // VIEW CERTIFICATE
    // ===============================
    public CertificateEntity viewCertificate(String email,
                                             String studentName,
                                             String courseName) {

        // 1️⃣ Enrollment Check
        admissionRepo.findByEmailAndCourseName(email, courseName)
                .orElseThrow(() ->
                        new RuntimeException("Student not enrolled in this course"));

        // 2️⃣ Fee Check
        if (!feeSer.isFeeComplete(email, courseName)) {
            throw new RuntimeException("Fee not completed");
        }

        // 3️⃣ Exam Check
        if (!submissionSer.isExamPassed(email, courseName)) {
            throw new RuntimeException("Exam not passed");
        }

        // 4️⃣ Course Completion Check
        if (!courseSer.isCourseComplete(email, courseName)) {
            throw new RuntimeException("Course not completed");
        }

        // 5️⃣ Check if already exists
        Optional<CertificateEntity> existing =
                cerRepo.findByEmailAndCourseName(email, courseName);

        if (existing.isPresent()) {
        	throw new RuntimeException("Certificate already generated for this student and course");
        }

        // 6️⃣ Create new certificate
        return createCertificate(email, studentName, courseName);
    }

    // ===============================
    // DOWNLOAD PDF
    // ===============================
    public byte[] downloadCertificatePdf(String email, String courseName) {

        CertificateEntity cert = cerRepo
                .findByEmailAndCourseName(email, courseName)
                .orElseThrow(() ->
                        new RuntimeException("Certificate not found"));

        return CertificatePdfGenerator.generate(cert);
    }

    // ===============================
    // CREATE CERTIFICATE
    // ===============================
    private CertificateEntity createCertificate(String email,
                                                String studentName,
                                                String courseName) {

        CertificateEntity c = new CertificateEntity();
        c.setEmail(email);
        c.setStudentName(studentName);
        c.setCourseName(courseName);

        // Better unique number
        c.setCertificateNo("ISEES-" +
                UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());

        c.setIssuedate(LocalDate.now());

        return cerRepo.save(c);
    }

	public List<CertificateEntity> getAllCerti() {
		// TODO Auto-generated method stub
		return cerRepo.findAll();
	}
	
	public List<CertificateEntity> getByEmail(String email){
	    return cerRepo.findByEmail(email);
	}
	
	// ===============================
	// VERIFY CERTIFICATE
	// ===============================

	public CertificateEntity verifyCertificate(String certificateNo){

	    return cerRepo.findByCertificateNo(certificateNo)
	            .orElseThrow(() ->
	                    new RuntimeException("Certificate Not Found"));
	}
}