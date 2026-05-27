package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Activity;
import com.example.service.ActivityService;

@RestController
@RequestMapping("/api/activity")
@CrossOrigin
public class ActivityController {
	
	@Autowired
	private ActivityService ser;
	
	// Add activity manually
    @PostMapping("/add")
    public String addActivity(@RequestBody Activity activity) {

        ser.saveActivity(
                activity.getEmail(),
                activity.getText()
        );

        return "Activity Added Successfully";
    }
    
 // Get recent activity
    @GetMapping("/get/{email}")
    public List<Activity> getRecentActivity(@PathVariable String email) {
        return ser.getStudentActivity(email);
    }
    
}
