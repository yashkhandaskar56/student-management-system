package com.example.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Assignment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Assignment {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String courseName;
    private String email; 
    private String status; 

    private String submissionText; 
    private String attachmentUrl; 

    private LocalDate dueDate;

}
