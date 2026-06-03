package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.Assignment;

public interface AssignmentRepo extends JpaRepository<Assignment, Long>{

	List<Assignment> findByEmail(String email);

}
