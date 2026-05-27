package com.example.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.dto.ExpenseReportDto;
import com.example.entity.ExpenseType;
import com.example.entity.ExpensesEntity;

public interface ExpenseRepo extends JpaRepository<ExpensesEntity, Long>{

	//List<ExpensesEntity> findByexpenseType(String expenseType);

	List<ExpensesEntity> findByExpenseType(ExpenseType type);
	
	 @Query("SELECT FUNCTION('MONTH', e.expenseDate), SUM(e.amount) " +
	           "FROM ExpensesEntity e " +
	           "WHERE e.expenseDate IS NOT NULL AND FUNCTION('YEAR', e.expenseDate) = :year " +
	           "GROUP BY FUNCTION('MONTH', e.expenseDate)")
	    List<Object[]> getMonthlyExpense(@Param("year") int year);

	    
	    @Query("""
	    	    SELECT COALESCE(SUM(e.amount),0)
	    	    FROM ExpensesEntity e
	    	    WHERE e.expenseDate >= :startDate
	    	      AND e.expenseDate <= :endDate
	    	""")
	    	double totalExpenseBetween(@Param("startDate") LocalDateTime startDate,
	    	                           @Param("endDate") LocalDateTime endDate);

		
	    @Query("""
	    	       SELECT e
	    	       FROM ExpensesEntity e
	    	       WHERE e.expenseDate BETWEEN :from AND :to
	    	    """)
	    	List<ExpensesEntity> findByExpenseDateBetween(
	    	        @Param("from") LocalDate from,
	    	        @Param("to") LocalDate to
	    	);

	

	    @Query("SELECT SUM(e.amount) FROM ExpensesEntity e WHERE MONTH(e.expenseDate)=MONTH(CURRENT_DATE)")
	    Double getMonthlyExpense();




}
