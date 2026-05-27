package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.Activity;


public interface ActivityRepo extends JpaRepository<Activity, Long>{

	List<Activity> findTop5ByEmailOrderByActivityDateDesc(String email);
	

}
