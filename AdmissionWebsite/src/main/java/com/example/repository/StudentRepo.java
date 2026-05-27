package com.example.repository;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.entity.Student;


public interface StudentRepo extends JpaRepository<Student, Long>{

	Optional<Student> findByEmail(String email);

	long count();


}
