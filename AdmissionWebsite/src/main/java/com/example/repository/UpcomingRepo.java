package com.example.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.UpcomingActivity;

public interface UpcomingRepo extends JpaRepository<UpcomingActivity, Long> {

	List<UpcomingActivity> findByEmailAndEventDateGreaterThanEqualOrderByEventDateAsc(String email, LocalDate now);

}
