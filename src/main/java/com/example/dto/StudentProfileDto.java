package com.example.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentProfileDto {

	  private String sname;
	  private String email;
	  private List<CourseInfo> courses;
}
