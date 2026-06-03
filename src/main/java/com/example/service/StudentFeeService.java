package com.example.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.example.dto.StudentFreeDto;
import com.example.entity.AdmissionApplication;
import com.example.entity.CourseList;
import com.example.entity.CourseStatus;
import com.example.entity.StudentFee;
import com.example.repository.AdmissionRepo;
import com.example.repository.CourseRepo;
import com.example.repository.StudentFeeRepo;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class StudentFeeService {

    @Autowired
    private AdmissionRepo arepo;
    @Autowired
    private CourseRepo crepo;
    @Autowired
	private JavaMailSender sender;
	@Autowired
	private Configuration config;
	@Autowired
	private StudentFeeRepo srepo;

    @Value("${stripe.secretKey}")
    private String secretKey;

    public Map<String, Object> processPayment(StudentFreeDto dto) throws Exception {
        Stripe.apiKey = secretKey;

        // ✅ Validate Student
        AdmissionApplication admission = arepo.findByEmailAndCourseName(dto.getEmail(), dto.getCourseName())
                .orElseThrow(() -> new RuntimeException("Student admission not found"));

        // ✅ Validate Course
        CourseList course = crepo.findByCourseName(dto.getCourseName())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        double totalFee = course.getFree();
        double oldPaid = admission.getPaidfree() != null ? admission.getPaidfree() : 0.0;

        // ✅ Validate paid fee
        if (dto.getPaidfee() == null || dto.getPaidfee() <= 0) {
            throw new IllegalArgumentException("Paid fee must be greater than 0");
        }

        double paid = dto.getPaidfee();
        double actualRemaining = totalFee - oldPaid;

        if (paid > actualRemaining) {
            throw new RuntimeException("Payment exceeds remaining fee");
        }
        
        double updatedPaid = oldPaid + paid;
        double remaining = totalFee - updatedPaid;

        // ✅ Stripe amount
        long amountPaise = Math.round(paid * 100);

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/success.html?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:8080/payment-failed.html")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("inr")
                                                .setUnitAmount(amountPaise)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Course Fee Payment: " + dto.getCourseName())
                                                                .build())
                                                .build())
                                .build())
                .putMetadata("email", dto.getEmail())
                .putMetadata("courseName", dto.getCourseName())
                .build();        

        Session session = Session.create(params);
        
        // ✅ Return Stripe Checkout URL
        Map<String, Object> response = new HashMap<>();
        response.put("checkoutUrl", session.getUrl());
        response.put("amountPaid", paid);
        response.put("remainingFee", remaining);
        response.put("sessionId", session.getId());

        return response;
    }   
    
    public Map<String,Object> verifyStripePayment(String sessionId) throws Exception {

        Stripe.apiKey = secretKey;

        Session session = Session.retrieve(sessionId);

        if (!"paid".equals(session.getPaymentStatus())) {
            throw new RuntimeException("Payment not completed");
        }

        String email = session.getMetadata().get("email");
        String courseName = session.getMetadata().get("courseName");

        AdmissionApplication admission =
                arepo.findByEmailAndCourseName(email, courseName)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        CourseList course =
                crepo.findByCourseName(courseName)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        double paid = session.getAmountTotal() / 100.0;

        double totalFee = course.getFree();
        double oldPaid = admission.getPaidfree() != null ? admission.getPaidfree() : 0;

        double updatedPaid = oldPaid + paid;
        double remaining = totalFee - updatedPaid;

        StudentFee history = new StudentFee();
        history.setEmail(email);
        history.setCourseName(courseName);
        history.setPaidfee(paid);
        history.setTotalfee(totalFee);
        history.setRemaningfee(remaining);
        history.setPaymentIntentId(sessionId);
        history.setPaymentdate(LocalDate.now());

        srepo.save(history);

        admission.setPaidfree(updatedPaid);
        admission.setRemainingfree(remaining);
        admission.setPaymentdate(LocalDate.now());
        admission.setCourseStatus(CourseStatus.IN_PROGRESS);

        arepo.save(admission);

        sendPaymentEmail(admission, history);

        Map<String,Object> response = new HashMap<>();

        response.put("sname", admission.getSname());
        response.put("email", admission.getEmail());
        response.put("courseName", admission.getCourseName());
        response.put("totalfree", totalFee);
        response.put("paidfree", updatedPaid);
        response.put("remainingfree", remaining);

        return response;
    }

    // ------------------------------
    // EMAIL METHOD
    // ------------------------------
    private void sendPaymentEmail(AdmissionApplication admission, StudentFee history) throws Exception {

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        Map<String, Object> model = new HashMap<>();
        model.put("student", admission.getSname());
        model.put("email", admission.getEmail());
        model.put("course", admission.getCourseName());
        model.put("paid", history.getPaidfee());
        model.put("remaining", history.getRemaningfee());
        model.put("total", history.getTotalfee());

        Template template = config.getTemplate("email-fee.ftl");
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

        helper.setTo(admission.getEmail());
        helper.setText(html, true);
        helper.setSubject("Payment Confirmation - " + admission.getCourseName());

        sender.send(message);
    }

    public File genreteReceipt(AdmissionApplication admission) throws FileNotFoundException, DocumentException {
    	String fileName = "receipt_" + admission.getEmail() + "_" + System.currentTimeMillis() + ".pdf";
        File pdfFile = new File(fileName);

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
        
        document.open();
        
        document.add(new Paragraph("ISEES TECHNOLOGIES"));
        document.add(new Paragraph("PYMENT RECEIPT"));
        document.add(new Paragraph("-----------------------------"));
        document.add(new Paragraph("Student Name: "+admission.getSname()));
        document.add(new Paragraph("Email: "+admission.getEmail()));
        document.add(new Paragraph("Course Name: "+admission.getCourseName()));
        document.add(new Paragraph("Total Fees: "+admission.getTotalfree()));
        document.add(new Paragraph("Paid Fees: "+admission.getPaidfree()));
        document.add(new Paragraph("Remaning Fees: "+admission.getRemainingfree()));
        document.add(new Paragraph("Payment Date: "+admission.getPaymentdate()));
        document.add(new Paragraph("-------------------------------"));
        document.add(new Paragraph("Thank you for Payment..."));
        document.add(new Paragraph("For any queries, contact support@iseestechnologies.com\""));
        
        document.close();
        
		return pdfFile;
    }

    public AdmissionApplication getStudentFeeDetails(String email, String courseName) {
        return arepo.findByEmailAndCourseName(email, courseName)
                .orElseThrow(() -> new RuntimeException("No record found for this student"));
    }
    
    public List<StudentFee> getPaymentHistory(String email){
        return srepo.findByEmail(email);
    }
    
    public Long getTotalFee() {
    	return arepo.getTotalFees();
    }
    
    public Long getPaidFee() {
    	return arepo.getTotalPaidFee();
    }
    
    public Long getRemeningFee() {
    	return arepo.getTotalRemainingFee();
    }

    public void sendReminderEmail(String email, String name, Double total, Double paid) throws MessagingException {

        double pending = total - paid;

        String subject = "Fee Payment Reminder";
        String body = "Hi " + name + ",\n\n"
                + "Total Fee: " + total + "\nPaid Fee: " + paid + "\nPending Fee: " + pending
                + "\n\nKindly clear your pending fee.\n\nThank You.";

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(body);

        sender.send(message);
    }
    
    public boolean isFeeComplete(String email, String courseName) {

        StudentFee fee = srepo
                .findTopByEmailAndCourseNameOrderByIdDesc(email, courseName)
                .orElseThrow(() ->
                        new RuntimeException("Fee record not found"));

        return fee.getRemaningfee() != null &&
               fee.getRemaningfee().doubleValue() == 0.0;
    }

    public StudentFee getPaymentHistoryBySessionId(String sessionId) {
        return srepo.findByPaymentIntentId(sessionId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
    
    
    public String offlinePaymentProcess(StudentFreeDto dto) throws Exception {

        // ✅ Validate admission
        AdmissionApplication admission = arepo
            .findByEmailAndCourseName(dto.getEmail(), dto.getCourseName())
            .orElseThrow(() -> new RuntimeException("Student admission not found"));

        // ✅ Validate course
        CourseList course = crepo
            .findByCourseName(dto.getCourseName())
            .orElseThrow(() -> new RuntimeException("Course not found"));

        double totalFee = course.getFree();
        double oldPaid = admission.getPaidfree() != null ? admission.getPaidfree() : 0.0;

        if (dto.getPaidfee() == null || dto.getPaidfee() <= 0) {
            throw new RuntimeException("Paid fee must be greater than 0");
        }

        double paid = dto.getPaidfee();
        double remainingBefore = totalFee - oldPaid;

        if (paid > remainingBefore) {
            throw new RuntimeException("Payment exceeds remaining fee");
        }

        double updatedPaid = oldPaid + paid;
        double remainingFee = totalFee - updatedPaid;
        
     

        // ✅ Save payment history
        StudentFee s = new StudentFee();
        s.setEmail(dto.getEmail());
        s.setCourseName(dto.getCourseName());
        s.setPaidfee(paid);
        s.setTotalfee(totalFee);
        s.setRemaningfee(remainingFee);
        s.setPaymentIntentId("offline");
        s.setPaymentdate(LocalDate.now());

        srepo.save(s);

        // ✅ Update existing admission (IMPORTANT)
        admission.setPaidfree(updatedPaid);
        admission.setRemainingfree(remainingFee);
        admission.setPaymentdate(LocalDate.now());

        arepo.save(admission);

        // ✅ Send email
        sendPaymentEmail(admission, s);

        return "offline payment recorded successfully. Receipt sent to email.";
    }
    
    public String onlinePaymentProcess(StudentFreeDto dto) throws Exception {

        // ✅ Validate admission
        AdmissionApplication admission = arepo
            .findByEmailAndCourseName(dto.getEmail(), dto.getCourseName())
            .orElseThrow(() -> new RuntimeException("Student admission not found"));

        // ✅ Validate course
        CourseList course = crepo
            .findByCourseName(dto.getCourseName())
            .orElseThrow(() -> new RuntimeException("Course not found"));

        double totalFee = course.getFree();
        double oldPaid = admission.getPaidfree() != null ? admission.getPaidfree() : 0.0;

        if (dto.getPaidfee() == null || dto.getPaidfee() <= 0) {
            throw new RuntimeException("Paid fee must be greater than 0");
        }

        double paid = dto.getPaidfee();
        double remainingBefore = totalFee - oldPaid;

        if (paid > remainingBefore) {
            throw new RuntimeException("Payment exceeds remaining fee");
        }

        double updatedPaid = oldPaid + paid;
        double remainingFee = totalFee - updatedPaid;
        
     // ✅ Stripe amount
        long amountPaise = Math.round(paid * 100);

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/success.html?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:8080/payment-failed.html")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("inr")
                                                .setUnitAmount(amountPaise)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Course Fee Payment: " + dto.getCourseName())
                                                                .build())
                                                .build())
                                .build())
                .putMetadata("email", dto.getEmail())
                .putMetadata("courseName", dto.getCourseName())
                .build();
        

        Session session = Session.create(params);

        // ✅ Save payment history
        StudentFee s = new StudentFee();
        s.setEmail(dto.getEmail());
        s.setCourseName(dto.getCourseName());
        s.setPaidfee(paid);
        s.setTotalfee(totalFee);
        s.setRemaningfee(remainingFee);
        s.setPaymentIntentId(session.getId());
        s.setPaymentdate(LocalDate.now());

        srepo.save(s);

        // ✅ Update existing admission (IMPORTANT)
        admission.setPaidfree(updatedPaid);
        admission.setRemainingfree(remainingFee);
        admission.setPaymentdate(LocalDate.now());

        arepo.save(admission);

        // ✅ Send email
        sendPaymentEmail(admission, s);

        return "Online payment recorded successfully. Receipt sent to email.";
    }


	public List<StudentFee> getAll() {
		// TODO Auto-generated method stub
		return srepo.findAll();
	}

    
	public List<StudentFee> getByCourseName(String courseName){
	    return srepo.findByCourseName(courseName);
	}
	
	public List<StudentFee> getPaymentHistoryFee(String email){
	    return srepo.findByEmail(email);
	}
	
	public List<StudentFee> getPendingFees(){
	    List<StudentFee> all = srepo.findAll();

	    return all.stream()
	            .filter(f -> f.getRemaningfee() > 0)
	            .toList();
	}


    
}
