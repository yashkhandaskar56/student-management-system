package com.example.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="Student_Notification")
@AllArgsConstructor
@NoArgsConstructor
public class AdminNotification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String email;
	private String title;
	private String message;
	
	@Enumerated(EnumType.STRING)
	private NotificationType type;
	
	@Column(name = "is_read")
	private boolean isRead=false;
	
	@Column(name = "created_at")
    private LocalDateTime createdAt;
	
}
