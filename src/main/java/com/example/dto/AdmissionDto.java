package com.example.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdmissionDto {
	
	//private Long studentId;
	private String sname;
	private String courseName;
	private String email;
	private String address;
	private String state;
	private String district;
	private String taluka;
	private String mobno;
	private String dob;
	private String gender;
	private MultipartFile photopath;
	

}
