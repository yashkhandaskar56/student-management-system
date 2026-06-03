package com.example.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.processing.Pattern;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
	    name = "admission_application",
	    uniqueConstraints = {
	        @UniqueConstraint(columnNames = {"mobno", "course_name"})
	    })
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdmissionApplication {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String sname;
	@Column(name = "course_name")
	private String courseName;

	@Column(name = "email")
	private String email;
	private String address;
	private String state;
	private String district;
	private String taluka;
	
	private String mobno;
	
	private String dob;
	private LocalDate createdAt;
	private String gender;
	private String photopath;
	private String signaturePhoto;
	private String adharCradPhoto;
	private String Marksheet;
	
	private Double admissionFromFee;
	private Boolean fromFeePaid;
	private String stripeSessionId;
	private LocalDate fromfeeDate;
	
	private Double totalfree;
	private Double paidfree;
	private Double remainingfree;

	private LocalDate paymentdate;
	
	@Enumerated(EnumType.STRING)   
    @Column(name = "course_status")
    private CourseStatus courseStatus;
	private LocalDate completionDate;
	
	@Column(name = "is_active")
	private Boolean isActive = true;	

}
