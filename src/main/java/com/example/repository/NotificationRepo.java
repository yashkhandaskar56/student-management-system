package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.entity.AdminNotification;

public interface NotificationRepo extends JpaRepository<AdminNotification, Long>{

//	List<AdminNotification> findByReadFalseOrderByCreatedAtDesc();

	long countByEmailAndIsReadFalse(String email);

	List<AdminNotification> findByEmailAndIsReadFalseOrderByCreatedAtDesc(String email);



}
