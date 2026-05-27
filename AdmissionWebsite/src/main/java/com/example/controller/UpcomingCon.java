package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.UpcomingActivity;
import com.example.service.UpcomingSer;

@RestController
@RequestMapping("api/upcoming")
@CrossOrigin
public class UpcomingCon {
	
	@Autowired
	private UpcomingSer ser;
	
	@PostMapping("/create")
	public UpcomingActivity create(@RequestBody UpcomingActivity a) {
		return ser.create(a);
	}
	
	@GetMapping("/get")
		public List<UpcomingActivity> getUpcoming(@RequestParam String email){
			return ser.getUpcoming(email);
	}
	

}
