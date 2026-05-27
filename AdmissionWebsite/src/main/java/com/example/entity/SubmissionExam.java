package com.example.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
public class SubmissionExam {
	
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long submissionId;

	    private String email;
	    private LocalDateTime submittedAt;
	    private Integer totalScore;
	    private Integer totalMarks;
	    private Double percentage;
	    private String grade;

	    @ManyToOne(fetch = FetchType.EAGER)
	    @JoinColumn(name = "exam_id")
	    private ExamEntity exam;

	    @OneToMany(mappedBy = "submission",
	            cascade = CascadeType.ALL,
	            fetch = FetchType.LAZY)
	    private List<SubmissionAnswerEntity> submissionAnswers;
    
}
