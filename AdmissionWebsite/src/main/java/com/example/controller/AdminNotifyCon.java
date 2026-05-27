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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.AdminNotification;
import com.example.entity.NotificationType;
import com.example.service.NotifySer;

@RestController
@RequestMapping("/api/admin/Notification")
@CrossOrigin
public class AdminNotifyCon {
	
	@Autowired
	private NotifySer ser;
	
	@PostMapping("/send-notify")
	public AdminNotification createNotify(@RequestParam String email,
			@RequestParam String title,
			@RequestParam String message,
			@RequestParam NotificationType type) {
	
		return ser.createNotify(email,title,message,type);
	}
		
//	@GetMapping("/unread")
//    public List<AdminNotification> getUnread() {
//        return ser.getUnread();
//    }
	
	@GetMapping("/get-all")
	public List<AdminNotification> getAllNotify(){
		return ser.getAllNotification();
	}
	
	@DeleteMapping
	public String clearAll() {
	   ser.clearAll();
	   return "Cleared";
	}

}
