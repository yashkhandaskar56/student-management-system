package com.example.service;

import java.time.LocalDate;
import java.util.List;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Activity;
import com.example.repository.ActivityRepo;

@Service
public class ActivityService {
	
	@Autowired
	private ActivityRepo repo;

	public void saveActivity(String email,String text){

        Activity a=new Activity();

        a.setEmail(email);
        a.setText(text);
        a.setActivityDate(LocalDate.now());

        repo.save(a);

    }
	
	public List<Activity> getStudentActivity(String email){

        return repo.findTop5ByEmailOrderByActivityDateDesc(email);

    }
	
}
