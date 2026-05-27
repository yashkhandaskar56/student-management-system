package com.example.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamEntity {
	
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long examId;

	    private String title;

	    private Long durationMinutes;
	    
	    private String courseName;

	    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
	    @JsonManagedReference
	    private List<QuestionEntity> questionList;

	
	


}
