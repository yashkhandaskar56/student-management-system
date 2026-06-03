package com.example.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
@Table(name = "optionsEntity")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long optionId;

    private String optionText;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @JsonBackReference
    private QuestionEntity question;
	

}
