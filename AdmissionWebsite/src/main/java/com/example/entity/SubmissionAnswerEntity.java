package com.example.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionAnswerEntity {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	 private Integer selectedOptionIndex;
	    private Boolean isCorrect;
	    private Integer obtainedMarks;

	    @ManyToOne
	    @JoinColumn(name = "question_id")
	    private QuestionEntity question;

	    @ManyToOne
	    @JoinColumn(name = "submission_id")
	    @JsonIgnore
	    private SubmissionExam submission;

}
