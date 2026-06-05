package com.example.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.dto.AdmissionDto;
import com.example.dto.CourseProgressDTO;
import com.example.dto.StudentProgressDTO;
import com.example.entity.AdmissionApplication;
import com.example.entity.CourseList;
import com.example.entity.CourseStatus;
import com.example.entity.StudentFee;
import com.example.repository.AdmissionRepo;
import com.example.repository.CourseRepo;
import com.example.repository.StudentFeeRepo;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;


import freemarker.template.Configuration;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;

@Service
public class AdmissionSer {
	
	@Autowired
	private CourseRepo crepo;
	@Autowired
	private AdmissionRepo arepo;
	@Autowired
	private JavaMailSender sender;
	@Autowired
	private Configuration config;
	@Autowired
	private StudentFeeRepo feeRepo;
	
	@Value("${stripe.secretKey}")
    private String secretKey;

	public AdmissionApplication applyfrom(
	        AdmissionDto dto,
	        MultipartFile photofile,
	        MultipartFile signaturePhoto,
	        MultipartFile adharCardPhoto,
	        MultipartFile marksheetPhoto
	) throws IOException, TemplateException {

	    // 1️⃣ VALIDATION
	    if (dto.getSname() == null || dto.getSname().trim().isEmpty() ||
	        dto.getCourseName() == null || dto.getCourseName().trim().isEmpty() ||
	        dto.getMobno() == null || !dto.getMobno().matches("\\d{10}") ||
	        dto.getState() == null || dto.getState().trim().isEmpty() ||
	        dto.getDistrict() == null || dto.getDistrict().trim().isEmpty() ||
	        dto.getTaluka() == null || dto.getTaluka().trim().isEmpty()) {

	        throw new RuntimeException("All fields are required and mobile number must be 10 digits");
	    }

	    Optional<AdmissionApplication> alreadyApplied =
	    	    arepo.findByMobnoAndCourseName(dto.getMobno(), dto.getCourseName());

	    	if(alreadyApplied.isPresent()){
	    	    throw new RuntimeException("You already applied for this course");
	    	}
	    
	    AdmissionApplication admission = new AdmissionApplication();

	 // always new record
	 admission.setCourseStatus(CourseStatus.FORM_FEE_PENDING);
	 admission.setCreatedAt(LocalDate.now());
	        
	        
	    // 3️⃣ COURSE CHECK
	    CourseList course = crepo.findByCourseName(dto.getCourseName())
	            .orElseThrow(() -> new RuntimeException("Course not found"));

	    if (course.getAvailableSeat() == null) {
	        course.setAvailableSeat(course.getTotalseat());
	    }

	    if (course.getAvailableSeat() <= 0) {
	        throw new RuntimeException("No seats available");
	    }
	    
	    
	 //  FILE TYPE VALIDATION 🔥🔥
        validateImageFile(photofile, "Student Photo");
        validateImageFile(signaturePhoto, "Signature Photo");
        validateDocumentFile(adharCardPhoto, "Aadhar Card");
        validateDocumentFile(marksheetPhoto, "Marksheet");

	    // CREATE ADMISSION ENTITY
	  //  AdmissionApplication admission = new AdmissionApplication();
	    admission.setSname(dto.getSname());
	    admission.setCourseName(dto.getCourseName());
	    admission.setEmail(dto.getEmail());
	    admission.setAddress(dto.getAddress());
	    admission.setState(dto.getState());
	    admission.setDistrict(dto.getDistrict());
	    admission.setTaluka(dto.getTaluka());
	    admission.setMobno(dto.getMobno());
	    admission.setDob(dto.getDob());
	    admission.setGender(dto.getGender());
	    
	    course.setTotalseat(course.getTotalseat());
	    course.setAvailableSeat(course.getTotalseat());

	    // FEES
	    admission.setTotalfree(course.getFree());
	    admission.setPaidfree(0.0);
	    admission.setRemainingfree(course.getFree());

	    double totalFee = admission.getTotalfree();      // e.g. 50000
	    double advanceFee = totalFee * 0.20;
	    admission.setAdmissionFromFee(advanceFee);
	    admission.setFromFeePaid(false);

	    // STATUS
	    admission.setCourseStatus(CourseStatus.FORM_FEE_PENDING);
	    admission.setCreatedAt(LocalDate.now());

	    
	 //  FILE UPLOAD
        String uploadDir = System.getProperty("java.io.tmpdir") + "/uploads";
        Files.createDirectories(Paths.get(uploadDir));

     // OLD DATA FETCH (IMPORTANT)
//        Optional<AdmissionApplication> existingOpt = 
//            arepo.findTopByMobnoOrderByIdDesc(dto.getMobno());

       

        if (photofile != null && !photofile.isEmpty()) {
            admission.setPhotopath(saveFile(uploadDir, photofile));
        }

        if (signaturePhoto != null && !signaturePhoto.isEmpty()) {
            admission.setSignaturePhoto(saveFile(uploadDir, signaturePhoto));
        }

        if (adharCardPhoto != null && !adharCardPhoto.isEmpty()) {
            admission.setAdharCradPhoto(saveFile(uploadDir, adharCardPhoto));
        }

        if (marksheetPhoto != null && !marksheetPhoto.isEmpty()) {
            admission.setMarksheet(saveFile(uploadDir, marksheetPhoto));
        }
        
        

	    // 6️⃣ SAVE
	    arepo.save(admission);

	    // 🔥 MOST IMPORTANT LINE
	    return admission;
	}

		
		// STRIPE PAYMENT SERVICE (FORM FEE)
	public String payFormFee(Long admissionId) throws Exception {

	    Stripe.apiKey = secretKey;

	    AdmissionApplication admission = arepo.findById(admissionId)
	            .orElseThrow(() -> new RuntimeException("Admission not found"));

	    // ✅ 20% ADVANCE CALCULATION
	    double totalFee = admission.getTotalfree();      // e.g. 50000
	    double advanceFee = totalFee * 0.20;             // 20% = 10000
	    double remainingFee = totalFee - advanceFee;     // 40000

	    // ✅ STRIPE NEEDS PAISE
	    long stripeAmount = (long) (advanceFee * 100);   // 10000 → 1000000 paise

	    Session session = Session.create(
	            SessionCreateParams.builder()
	                    .setMode(SessionCreateParams.Mode.PAYMENT)
	                    .setSuccessUrl(
	                        "http://localhost:8080/admission/payment-success?session_id={CHECKOUT_SESSION_ID}"
	                    )
	                    .setCancelUrl("http://localhost:8080/payment-failed")

	                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
//	                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.UPI)
//	                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.NETBANKING)

	                    .addLineItem(
	                            SessionCreateParams.LineItem.builder()
	                                    .setQuantity(1L)
	                                    .setPriceData(
	                                            SessionCreateParams.LineItem.PriceData.builder()
	                                                    .setCurrency("inr")
	                                                    .setUnitAmount(stripeAmount)
	                                                    .setProductData(
	                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
	                                                                    .setName("Admission Advance Fee (20%)")
	                                                                    .build())
	                                                    .build())
	                                    .build())
	                    .build()
	    );

//	    // ✅ SAVE STRIPE SESSION
//	    admission.setTotalfree(totalFee);
//	    admission.setPaidfree(advanceFee);
//	    admission.setRemainingfree(remainingFee);
//	    admission.setStripeSessionId(session.getId());
//	    admission.setPaymentdate(LocalDate.now());
//	    arepo.save(admission);
//
//	    // ✅ SAVE STUDENT FEE RECORD
//	    StudentFee f = new StudentFee();
//	    f.setEmail(admission.getEmail());
//	    f.setCourseName(admission.getCourseName());
//	    f.setPaidfee(advanceFee);
//	    f.setRemaningfee(remainingFee);
//	    f.setTotalfee(totalFee);
//	    f.setPaymentIntentId(session.getId());
//	    f.setPaymentdate(LocalDate.now());
//	    feeRepo.save(f);
	    
	    admission.setStripeSessionId(session.getId());
	    arepo.save(admission);

	    return session.getUrl();
	}

		
//		PAYMENT SUCCESS LOGIC
		@Transactional
		public void handleFormFeeSuccess(String sessionId)
		        throws IOException, TemplateException {

		    // ✅ DIRECT DB QUERY (NO STREAM)
		    AdmissionApplication admission = arepo.findByStripeSessionId(sessionId)
		            .orElseThrow(() ->
		                new RuntimeException("Invalid or expired Stripe session")
		            );

		    // ✅ ALREADY PAID CHECK (IMPORTANT)
		    if (Boolean.TRUE.equals(admission.getFromFeePaid())) {
		        System.out.println("⚠️ Form fee already paid for admission ID: " + admission.getId());
		        return;
		    }
		    
		    double totalFee = admission.getTotalfree();      // e.g. 50000
		    double advanceFee = totalFee * 0.20;             // 20% = 10000
		    double remainingFee = totalFee - advanceFee;

		    // ✅ UPDATE PAYMENT INFO
		    admission.setFromFeePaid(true);
		    admission.setFromfeeDate(LocalDate.now());

		    admission.setPaidfree(advanceFee);
		    admission.setRemainingfree(remainingFee);
		    
		    CourseList course = crepo.findByCourseName(admission.getCourseName())
		            .orElseThrow(() -> new RuntimeException("Course not found"));
		    
		    course.setAvailableSeat(course.getAvailableSeat() - 1);
		    crepo.save(course);

		    admission.setCourseStatus(CourseStatus.IN_PROGRESS);

		    arepo.save(admission);
		    
		    StudentFee f = new StudentFee();
		    f.setEmail(admission.getEmail());
		    f.setCourseName(admission.getCourseName());
		    f.setPaidfee(advanceFee);
		    f.setRemaningfee(remainingFee);
		    f.setTotalfee(totalFee);
		    f.setPaymentIntentId(sessionId);
		    f.setPaymentdate(LocalDate.now());

		    feeRepo.save(f);

		    // ✅ EMAIL
		    sendConfirmationMail(admission);
		}
		
		private void sendConfirmationMail(AdmissionApplication admission)
		        throws IOException, TemplateException {

		    if (admission.getEmail() == null || admission.getEmail().isEmpty()) return;

		    MimeMessage message = sender.createMimeMessage();
		    try {
		        MimeMessageHelper helper =
		                new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED);

		        Map<String, Object> model = new HashMap<>();
		        model.put("title", "ISEES TECHNOLOGIES Admission Confirmation");
		        model.put("name", admission.getSname());
		        model.put("content", "🎉 Your admission form fee has been successfully paid.");

		        Template template = config.getTemplate("template.ftl");
		        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

		        helper.setTo(admission.getEmail());
		        helper.setSubject("Admission Form Fee Payment Successful");
		        helper.setText(html, true);

		        sender.send(message);
		        System.out.println("✅ Email sent to " + admission.getEmail());

		    } catch (MessagingException e) {
		        System.err.println("❌ Email failed: " + e.getMessage());
		    }
		}
		
		// ================= VALIDATION METHODS =================
		private void validateImageFile(MultipartFile file, String fieldName) {
		    if (file == null || file.isEmpty()) {
		        return; // ✅ skip validation for old student
		    }

		    String type = file.getContentType();
		    if (type == null || !(type.equals("image/jpeg") || type.equals("image/png"))) {
		        throw new RuntimeException(fieldName + " must be JPG or PNG image only");
		    }
		}

		private void validateDocumentFile(MultipartFile file, String fieldName) {
		    if (file == null || file.isEmpty()) {
		        return; // ✅ skip validation
		    }

		    String type = file.getContentType();
		    if (type == null ||
		       !(type.equals("image/jpeg") ||
		         type.equals("image/png") ||
		         type.equals("application/pdf"))) {

		        throw new RuntimeException(fieldName + " must be PDF, JPG or PNG only");
		    }
		}



		
		
		public List<AdmissionApplication> getAllAdmission(){
			return arepo.findAll();
		}
	
		public Optional<AdmissionApplication> getStudentApplication(Long id){
			return arepo.findById(id);
		}
		
		public ResponseEntity<List<AdmissionApplication>> getCourse(String email){
			List<AdmissionApplication> list=arepo.findByEmail(email);
			if(list.isEmpty()) {
				return ResponseEntity.noContent().build();
			}
				return ResponseEntity.ok(list);
		}
		
		public Long gettotalAdmission() {
			return arepo.gettotalAdmission();
		}
		
		public Long getAllInProgessAdmission() {
			return arepo.countInProgressStudents();
		}
		
private String saveFile(String dir, MultipartFile file) throws IOException {

    File uploadFolder = new File(dir);

    if (!uploadFolder.exists()) {
        uploadFolder.mkdirs();
    }

    String filename =
            UUID.randomUUID() + "_" + file.getOriginalFilename();

    File destination =
            new File(uploadFolder, filename);

    file.transferTo(destination);

    return destination.getAbsolutePath();
}
		
		 public AdmissionApplication getCourseById(Long id) {

		        Optional<AdmissionApplication> optional = arepo.findById(id);

		        if(optional.isPresent()) {
		            return optional.get();
		        } else {
		            throw new RuntimeException("Course not found with id: " + id);
		        }
		    }

		 public AdmissionApplication getStudentByEmail(String email){
			    return arepo.findTopByEmailOrderByIdDesc(email);
			}
		 
		 public List<StudentProgressDTO> getStudentsWithProgress(){

			    List<AdmissionApplication> students = arepo.findAll();

			    List<StudentProgressDTO> result = new ArrayList<>();

			    for(AdmissionApplication s : students){

			        StudentProgressDTO dto = new StudentProgressDTO();

			        dto.setId(s.getId());
			        dto.setSname(s.getSname());
			        dto.setEmail(s.getEmail());
			        dto.setMobno(s.getMobno());
			        dto.setCourseName(s.getCourseName());

			        CourseList course =
			                crepo.findByCourseName(s.getCourseName())
			                .orElse(null);

			        Long totalHours = 0L;

			        Integer dailyHours = 0;

			        if(course != null){

			            totalHours =
			                    course.getTotalHours() == null
			                    ? 0
			                    : course.getTotalHours();

			            dailyHours =
			                    course.getDailyHours() == null
			                    ? 0
			                    : course.getDailyHours();
			        }

			        // 🔥 DAYS PASSED
			        long daysPassed = 0;

			        if(s.getCreatedAt() != null){

			            daysPassed =
			                java.time.temporal.ChronoUnit.DAYS
			                .between(s.getCreatedAt(), LocalDate.now());

			            if(daysPassed < 0){
			                daysPassed = 0;
			            }
			        }

			        // 🔥 AUTO COMPLETED HOURS
			        long completedHours =
			                daysPassed * dailyHours;

			        // total hours cross nahi hona chahiye
			        if(completedHours > totalHours){
			            completedHours = totalHours;
			        }

			        long remainingHours =
			                totalHours - completedHours;

			        double progress = 0;

			        if(totalHours > 0){

			            progress =
			                    (completedHours * 100.0)
			                    / totalHours;
			        }

			        dto.setTotalHours(totalHours);
			        dto.setCompletedHours((int) completedHours);
			        dto.setRemainingHours((long) remainingHours);
			        dto.setProgress(progress);

			        dto.setCourseStatus(s.getCourseStatus());
			        dto.setIsActive(s.getIsActive());
			        dto.setCreatedAt(s.getCreatedAt()); 

			        result.add(dto);
			    }

			    return result;
			}


		 public List<CourseProgressDTO> getCourseProgress(String email) {
			 
			 List<AdmissionApplication> list = arepo.findByEmail(email); 
			 
			 List<CourseProgressDTO> result = new ArrayList<>(); 
			 
			 for (AdmissionApplication a : list) {
				 int progress = 0;
				 if (a.getTotalfree() != null && a.getTotalfree() > 0) {
					 progress = (int)((a.getPaidfree() * 100) / a.getTotalfree());
				
				}
				 result.add(new CourseProgressDTO(a.getCourseName(), progress));
				} 
			 return result; 
			}
		 
		 public List<StudentProgressDTO> getStudentProgressByEmail(String email){

			    List<AdmissionApplication> students =
			            arepo.findByEmail(email);

			    List<StudentProgressDTO> result =
			            new ArrayList<>();

			    for(AdmissionApplication s : students){

			        StudentProgressDTO dto =
			                new StudentProgressDTO();

			        dto.setId(s.getId());
			        dto.setSname(s.getSname());
			        dto.setEmail(s.getEmail());
			        dto.setMobno(s.getMobno());
			        dto.setCourseName(s.getCourseName());

			        CourseList course =
			                crepo.findByCourseName(s.getCourseName())
			                .orElse(null);

			        Long totalHours = 0L;

			        Integer dailyHours = 0;

			        if(course != null){

			            totalHours =
			                    course.getTotalHours() == null
			                    ? 0
			                    : course.getTotalHours();

			            dailyHours =
			                    course.getDailyHours() == null
			                    ? 0
			                    : course.getDailyHours();
			        }

			        long daysPassed = 0;

			        if(s.getCreatedAt() != null){

			            daysPassed =
			                    java.time.temporal.ChronoUnit.DAYS
			                    .between(s.getCreatedAt(), LocalDate.now());

			            if(daysPassed < 0){
			                daysPassed = 0;
			            }
			        }

			        long completedHours =
			                daysPassed * dailyHours;

			        if(completedHours > totalHours){
			            completedHours = totalHours;
			        }

			        long remainingHours =
			                totalHours - completedHours;

			        double progress = 0;

			        if(totalHours > 0){

			            progress =
			                    (completedHours * 100.0)
			                    / totalHours;
			        }

			        dto.setTotalHours(totalHours);
			        dto.setCompletedHours((int) completedHours);
			        dto.setRemainingHours(remainingHours);
			        dto.setProgress(progress);

			        dto.setCourseStatus(s.getCourseStatus());
			        dto.setIsActive(s.getIsActive());
			        dto.setCreatedAt(s.getCreatedAt());

			        result.add(dto);
			    }

			    return result;
			}

		 public String importExcel(MultipartFile file) {

			    int successCount = 0;

			    try {

			        // 🔥 FILE EMPTY CHECK
			        if (file.isEmpty()) {
			            return "Excel File Empty";
			        }

			        // 🔥 FILE TYPE CHECK
			        String filename = file.getOriginalFilename();

			        if (filename == null ||
			                (!filename.endsWith(".xlsx")
			                && !filename.endsWith(".xls"))) {

			            return "Only Excel Files Allowed";
			        }

			        InputStream is = file.getInputStream();

			        Workbook workbook = WorkbookFactory.create(is);

			        Sheet sheet = workbook.getSheetAt(0);

			        boolean firstRow = true;

			        for (Row row : sheet) {

			            // 🔥 SKIP HEADER
			            if (firstRow) {
			                firstRow = false;
			                continue;
			            }

			            // 🔥 EMPTY ROW SKIP
			            if (row == null || row.getCell(0) == null) {
			                continue;
			            }

			            try {

			                AdmissionApplication app =
			                        new AdmissionApplication();

			                // 🔥 BASIC DATA
			                app.setSname(
			                        getCellValue(row.getCell(0)));

			                app.setCourseName(
			                        getCellValue(row.getCell(1)));

			                app.setEmail(
			                        getCellValue(row.getCell(2)));

			                app.setAddress(
			                        getCellValue(row.getCell(3)));

			                app.setState(
			                        getCellValue(row.getCell(4)));

			                app.setDistrict(
			                        getCellValue(row.getCell(5)));

			                app.setTaluka(
			                        getCellValue(row.getCell(6)));

			                app.setMobno(
			                        getCellValue(row.getCell(7)));

			                app.setDob(
			                        getCellValue(row.getCell(8)));

			                app.setGender(
			                        getCellValue(row.getCell(9)));

			                // 🔥 FEES
			                Double totalFee =
			                        getDoubleValue(row.getCell(10));

			                Double paidFee =
			                        getDoubleValue(row.getCell(11));

			                app.setTotalfree(totalFee);

			                app.setPaidfree(paidFee);

			                app.setRemainingfree(
			                        totalFee - paidFee);

			                // 🔥 CREATED DATE
			                String createdDate =
			                        getCellValue(row.getCell(13));

			                if (!createdDate.isEmpty()) {

			                    app.setCreatedAt(
			                            LocalDate.parse(createdDate));

			                } else {

			                    app.setCreatedAt(LocalDate.now());
			                }

			                // 🔥 PAYMENT DATE
			                String paymentDate =
			                        getCellValue(row.getCell(14));

			                if (!paymentDate.isEmpty()) {

			                    app.setPaymentdate(
			                            LocalDate.parse(paymentDate));
			                }

			                // 🔥 COURSE STATUS
			                String status =
			                        getCellValue(row.getCell(15));

			                if (!status.isEmpty()) {

			                    app.setCourseStatus(
			                            CourseStatus.valueOf(status));

			                } else {

			                    app.setCourseStatus(
			                            CourseStatus.IN_PROGRESS);
			                }

			                // 🔥 ACTIVE STATUS
			                String active =
			                        getCellValue(row.getCell(16));

			                if (!active.isEmpty()) {

			                    app.setIsActive(
			                            Boolean.parseBoolean(active));

			                } else {

			                    app.setIsActive(true);
			                }

			                // 🔥 FORM FEE
			                app.setFromFeePaid(true);

			                // 🔥 ADMISSION FORM FEE AUTO CALCULATE
			                app.setAdmissionFromFee(
			                        totalFee * 0.20);

			                // 🔥 MOBILE VALIDATION
			                if (app.getMobno() == null
			                        || app.getMobno().length() != 10) {

			                    continue;
			                }

			                // 🔥 EMAIL VALIDATION
			                if (app.getEmail() == null
			                        || !app.getEmail().contains("@")) {

			                    continue;
			                }

			                // 🔥 COURSE EXIST CHECK
			                Optional<CourseList> course =
			                        crepo.findByCourseName(
			                                app.getCourseName());

			                if (course.isEmpty()) {

			                    continue;
			                }

			                // 🔥 DUPLICATE CHECK
			                Optional<AdmissionApplication> exists =
			                        arepo.findByMobnoAndCourseName(
			                                app.getMobno(),
			                                app.getCourseName());

			                // 🔥 SAVE ONLY NEW
			                if (exists.isEmpty()) {

			                    arepo.save(app);

			                    successCount++;
			                }

			            } catch (Exception e) {

			                System.out.println(
			                        "❌ Row Skip Error: "
			                                + e.getMessage());
			            }
			        }

			        workbook.close();

			    } catch (Exception e) {

			        e.printStackTrace();

			        return "Import Failed";
			    }

			    return successCount
			            + " Students Imported Successfully";
			}


			private String getCellValue(Cell cell) {

			    if (cell == null) {
			        return "";
			    }

			    if (cell.getCellType() == CellType.STRING) {

			        return cell.getStringCellValue().trim();

			    } else if (cell.getCellType() == CellType.NUMERIC) {

			        if (DateUtil.isCellDateFormatted(cell)) {

			            return cell.getLocalDateTimeCellValue()
			                    .toLocalDate()
			                    .toString();
			        }

			        return String.valueOf(
			                (long) cell.getNumericCellValue());
			    }

			    return "";
			}


			private Double getDoubleValue(Cell cell) {

			    if (cell == null) {
			        return 0.0;
			    }

			    try {

			        if (cell.getCellType() == CellType.NUMERIC) {

			            return cell.getNumericCellValue();
			        }

			        return Double.parseDouble(
			                cell.getStringCellValue());

			    } catch (Exception e) {

			        return 0.0;
			    }
			}
		 
}
