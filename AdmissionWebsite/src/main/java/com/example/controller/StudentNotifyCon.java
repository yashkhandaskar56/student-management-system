package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.AdminNotification;
import com.example.service.NotifySer;

@RestController
@RequestMapping("/api/student/notification")
@CrossOrigin
public class StudentNotifyCon {
	
	@Autowired
	private NotifySer ser;
	
	@GetMapping("/get-all")
	public List<AdminNotification> getAllNotify(@RequestParam String email){
		return ser.getAllNotify(email);
	}
	
	@GetMapping("/count")
    public long unreadCount(@RequestParam String email) {
        return ser.unreadCount(email);
    }

    @PutMapping("/{id}/read")
    public String markRead(@PathVariable Long id) {
        ser.markAsRead(id);
        return "Read";
    }

}
