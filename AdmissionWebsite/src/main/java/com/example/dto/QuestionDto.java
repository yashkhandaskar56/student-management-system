package com.example.dto;

import java.util.List;

import com.example.entity.OptionEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDto {
	
	private Long questionId;
	private String question;
	private Integer marks;
	private Integer correctOptionIndex;
	
	
	private List<OptionDto> optionList;

}
