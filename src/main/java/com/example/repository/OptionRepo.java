package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.OptionEntity;

public interface OptionRepo extends JpaRepository<OptionEntity, Long>{

}
