package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDetailsDTO {
	
	 private Long id;

	    private String sname;
	    private String email;
	    private String mobno;

	    private String courseName;

	    private String address;
	    private String state;
	    private String district;
	    private String taluka;

	    private String dob;
	    private String gender;

	    private String photopath;
	    private String signaturePhoto;
	    private String adharCradPhoto;
	    private String marksheet;

	    private Double totalfree;
	    private Double paidfree;
	    private Double remainingfree;

	    private Integer totalHours;
	    private Integer completedHours;
	    private Integer remainingHours;

	    private Double progress;

	    private Boolean isActive;

	    private String courseStatus;

	    private String createdAt;

	    private Integer courseDuration;

}
