package com.example.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class adminCard {
	
	private long totalStudents;
    private long currentAdmissions;
    private long totalCourses;
    private long totalExams;
    

    private double pendingFees;
    private double monthlyIncome;
    private double monthlyExpense;
    
    private long totalcertificate;

}
