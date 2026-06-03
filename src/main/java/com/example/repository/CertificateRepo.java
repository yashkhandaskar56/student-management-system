package com.example.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.entity.CertificateEntity;

public interface CertificateRepo extends JpaRepository<CertificateEntity, Long>{

	Optional<CertificateEntity> findByEmailAndCourseName(
            String email, String courseName);
	
	@Query(value = "SELECT COUNT(*) FROM certificate_entity WHERE email = :email", nativeQuery = true)
    long countCertificates(@Param("email") String email);

	List<CertificateEntity> findByEmail(String email);
	
	long count();

	List<CertificateEntity> findByIssuedateBetween(LocalDate from, LocalDate to);
	
	  Optional<CertificateEntity>
	    findByCertificateNo(String certificateNo);



}
