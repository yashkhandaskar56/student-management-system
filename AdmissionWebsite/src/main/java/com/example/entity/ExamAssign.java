package com.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
	    uniqueConstraints = @UniqueConstraint(columnNames = {"studentEmail", "exam_id"})
	)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamAssign {
	
	@Id
    @GeneratedValue(strategy = GenerationType
    .IDENTITY)
    private Long id;

    private String studentEmail;

    private boolean available = true;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private ExamEntity exam;

}
