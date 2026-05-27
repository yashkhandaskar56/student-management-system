package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.AdminEntity;

public interface AdminRepo extends JpaRepository<AdminEntity, Long>{

	Optional<AdminEntity> findByEmail(String email);

}
