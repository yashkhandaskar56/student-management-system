package com.example.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.AdminNotification;
import com.example.entity.NotificationType;
import com.example.repository.NotificationRepo;

@Service
public class NotifySer {
	
	@Autowired
	private NotificationRepo repo;
	
	// Admin create Notification//
	public AdminNotification createNotify(String email,
			String title,
			String message,
			NotificationType type) {
		
		AdminNotification n=new AdminNotification();
		n.setEmail(email);
		n.setTitle(title);
		n.setMessage(message);
		n.setType(type);
		n.setCreatedAt(LocalDateTime.now());
		
		return repo.save(n);
		
	}
	
	// get All Notification//
	public List<AdminNotification> getAllNotify(String email){
		return repo.findByEmailAndIsReadFalseOrderByCreatedAtDesc(email);
	}
	
//	public List<AdminNotification> getUnread() {
//        return repo.findByReadFalseOrderByCreatedAtDesc();
//    }

	public long unreadCount(String email) {
		// TODO Auto-generated method stub
		return repo.countByEmailAndIsReadFalse(email);
	}

	public void markAsRead(Long id) {
		// TODO Auto-generated method stub
		
		AdminNotification n=repo.findById(id).orElseThrow(()-> new RuntimeException("Not Found"));
		
		n.setRead(true);
		repo.save(n);
		
	}

	public void clearAll() {
		// TODO Auto-generated method stub
		repo.deleteAll();
	}

	public List<AdminNotification> getAllNotification() {
		// TODO Auto-generated method stub
		return repo.findAll();
	}

}
