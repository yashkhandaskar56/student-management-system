package com.example.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseMaterialDto {

	private String courseName;
	private String title;
	private String description;
	private String filepath;
	private String filename;
	private LocalDateTime uploadedAt;
}
