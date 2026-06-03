package com.example.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.entity.ExamEntity;
import com.example.entity.SubmissionExam;

public interface SubmissionRepo extends JpaRepository<SubmissionExam, Long>{

	List<SubmissionExam> findByExam(ExamEntity exam);
	
	@Query("SELECT COUNT(s) FROM SubmissionExam s WHERE s.submittedAt>= :start AND s.submittedAt<= :end")
	Long countAttemptToday(LocalDateTime start,LocalDateTime end);
	
	@Query("SELECT COUNT(s) FROM SubmissionExam s WHERE s.percentage >= 40")
    Long countPassedStudents();
	
	@Query("SELECT COUNT (s.title) FROM ExamEntity s ")
	Long countTotalExam();
	
	@Query("SELECT COUNT(s) FROM SubmissionExam s")
	long countTotalAttempt();
	
	@Query("SELECT COUNT(s) FROM SubmissionExam s WHERE s.percentage <= 40")
	Double findFailedPercentage();
	
	@Query("SELECT COUNT(s)FROM SubmissionExam s WHERE s.email=:email")
	Long CountByExamStudentEmail(String email);
	
	 // Student-wise attempt
    @Query("SELECT COUNT(s) FROM SubmissionExam s WHERE s.email = :email")
    Long countAttemptedByStudent(String email);

    // Passed
    @Query("""
        SELECT COUNT(s) FROM SubmissionExam s 
        WHERE s.email = :email AND s.grade <> 'Fail'
    """)
    Long countPassedByStudent(String email);

    // Failed
    @Query("""
        SELECT COUNT(s) FROM SubmissionExam s 
        WHERE s.email = :email AND s.grade = 'Fail'
    """)
    Long countFailedByStudent(String email);

    // Latest submission by student email
    Optional<SubmissionExam> findTopByEmailOrderBySubmittedAtDesc(String email);

 // ALL attempts by student
    List<SubmissionExam> findByEmailOrderBySubmittedAtDesc(String email);

	Optional<SubmissionExam> findTopByEmailAndExam_CourseNameOrderBySubmittedAtDesc(String email, String courseName);

	Optional<SubmissionExam> findBySubmissionId(Long submissionId);
	
	@Query(value = "SELECT COUNT(*) FROM submission_exam WHERE email = :email", nativeQuery = true)
    long countExamAttempts(@Param("email") String email);
	
	Optional<SubmissionExam> findByEmailAndExam(String email, ExamEntity exam);

	

	boolean existsByEmailAndExam_ExamId(String email, Long examId);

	@Query("SELECT s FROM SubmissionExam s WHERE s.submittedAt BETWEEN :from AND :to")
	List<SubmissionExam> findExamReport(LocalDateTime from, LocalDateTime to);

}
