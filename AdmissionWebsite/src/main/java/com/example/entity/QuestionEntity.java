package com.example.entity;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "questionsEntity")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long questionId;
	private String question;
	private Integer marks;
	private Integer correctOptionIndex;
	
	@ManyToOne
	@JoinColumn(name = "exam_id")
	@JsonBackReference   
	private ExamEntity exam;
	 
	@OneToMany(mappedBy = "question",
	           cascade = CascadeType.ALL,
	           orphanRemoval = true)
	private List<OptionEntity> optionList = new ArrayList<>();

	
}
