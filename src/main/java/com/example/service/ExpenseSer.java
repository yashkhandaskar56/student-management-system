package com.example.service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dto.ExpenseDto;
import com.example.entity.ExpenseType;
import com.example.entity.ExpensesEntity;
import com.example.repository.ExpenseRepo;

@Service
public class ExpenseSer {
	
	@Autowired
	private ExpenseRepo repo;
	
	 // ADD EXPENSE
    public ExpensesEntity addExpense(ExpenseDto dto){

        ExpensesEntity e = new ExpensesEntity();

        e.setExpenseType(dto.getExpenseType());
        e.setTitle(dto.getTitle());
        e.setDescription(dto.getDescription());
        e.setAmount(dto.getAmount());
        e.setPaidTo(dto.getPaidTo());
        e.setPaymentMethod(dto.getPaymentMethod());
        e.setBillNumber(dto.getBillNumber());
        
        if(dto.getExpenseDate() != null){
            e.setExpenseDate(dto.getExpenseDate());
        }else{
            e.setExpenseDate(LocalDate.now());
        }

        e.setCreatedBy("Admin");

        e.setCreatedAt(LocalDateTime.now());

        return repo.save(e);
    }
	
	public List<ExpensesEntity> getAll(){
		return repo.findAll();
	}
	
	public ExpensesEntity getExpenseById(Long id){

	    return repo.findById(id)
	            .orElseThrow(() ->
	            new RuntimeException("Expense Not Found"));
	}

	public ExpensesEntity updateExpense(Long id, ExpenseDto dto) {
		// TODO Auto-generated method stub
		ExpensesEntity e=repo.findById(id).orElseThrow(()->
									new RuntimeException("Expense Not Found with Id="+id));
		
		 e.setExpenseType(dto.getExpenseType());
	        e.setTitle(dto.getTitle());
	        e.setDescription(dto.getDescription());
	        e.setAmount(dto.getAmount());
	        e.setPaidTo(dto.getPaidTo());
	        e.setPaymentMethod(dto.getPaymentMethod());
	        e.setBillNumber(dto.getBillNumber());
	        
	        if(dto.getExpenseDate() != null){
	            e.setExpenseDate(dto.getExpenseDate());
	        }else{
	            e.setExpenseDate(LocalDate.now());
	        }

	        e.setCreatedBy("Admin");

	        e.setCreatedAt(LocalDateTime.now());

		return repo.save(e);
		 
	}
	
	public void deleteExpense(Long id) {
        ExpensesEntity expense = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id " + id));
        repo.delete(expense);
    }

//	public List<ExpensesEntity> getExpenseType(String expenseType) {
//		// TODO Auto-generated method stub
//		return repo.findByexpenseType(expenseType);
//	}

	public List<ExpensesEntity> getExpensesByType(ExpenseType type) {
        return repo.findByExpenseType(type);
    }

}
