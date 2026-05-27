package com.example.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.dto.FeeReportDto;
import com.example.entity.AdmissionApplication;
import com.example.entity.StudentFee;

public interface StudentFeeRepo extends JpaRepository<StudentFee, Long>{

	List<StudentFee> findByEmail(String email);

	Optional<StudentFee>
	findTopByEmailAndCourseNameOrderByIdDesc(String email, String courseName);

	Optional<StudentFee> findByPaymentIntentId(String sessionId);

	@Query("""
			   SELECT s
			   FROM StudentFee s
			   WHERE s.paymentdate BETWEEN :from AND :to
			""")
			List<StudentFee> findBypaymentdateBetween(
			        @Param("from") LocalDate from,
			        @Param("to") LocalDate to
			);

	List<StudentFee> findByCourseName(String courseName);


	

	
}
