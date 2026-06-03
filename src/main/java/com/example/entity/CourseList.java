package com.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="course_list")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseList {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String courseName;
	private Long availableSeat;
	private Long totalseat;
	
	private Integer courseDurationMonths;
	private Long totalHours;
	private Integer dailyHours;

	
	private Double free;
	
	private String description;
	
}
