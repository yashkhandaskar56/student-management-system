package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.ExpenseDto;
import com.example.entity.ExpenseType;
import com.example.entity.ExpensesEntity;
import com.example.service.ExpenseSer;

@RestController
@RequestMapping("api/expense")
@CrossOrigin(origins = "*")
public class ExpenseCon {
	
	@Autowired
	private ExpenseSer ser;
	
	
	@PostMapping("/upload")
	public ExpensesEntity uploadExpense(@RequestBody ExpenseDto  dto) {
		return ser.addExpense(dto);
	}
	
	@GetMapping("/{id}")
	public ExpensesEntity getExpenseById(@PathVariable Long id){
	    return ser.getExpenseById(id);
	}
	
	@GetMapping("/all")
	public List<ExpensesEntity> getAll(){
		return ser.getAll();
	}
	
	@PutMapping("/update/{id}")
	public ExpensesEntity updateExpense(@PathVariable Long id,@RequestBody ExpenseDto dto) {
		return ser.updateExpense(id,dto);
	}
	
	@DeleteMapping("/delete/{id}")
	public String deleteExpense(@PathVariable Long id) {
		ser.deleteExpense(id);
		return "Expense deleted successfully";
	}
	
	@GetMapping("/type/{expenseType}")
	public List<ExpensesEntity> getExpensesByType(@PathVariable String expenseType) {
	    ExpenseType type;
	    try {
	        type = ExpenseType.valueOf(expenseType.toUpperCase()); // convert to enum
	    } catch (IllegalArgumentException e) {
	        throw new RuntimeException("Invalid expense type: " + expenseType);
	    }
	    
	    return ser.getExpensesByType(type);
	}


}
