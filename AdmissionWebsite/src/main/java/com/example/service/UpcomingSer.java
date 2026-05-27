package com.example.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.UpcomingActivity;
import com.example.repository.UpcomingRepo;

@Service
public class UpcomingSer {

	@Autowired
	private UpcomingRepo repo;
	
	public List<UpcomingActivity> getUpcoming(String email){
        return repo.findByEmailAndEventDateGreaterThanEqualOrderByEventDateAsc(
            email, LocalDate.now());
    }

    //  create
    public UpcomingActivity create(UpcomingActivity a){
        return repo.save(a);
    }
}
