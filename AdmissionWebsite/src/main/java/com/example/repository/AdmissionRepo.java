package com.example.repository;



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.dto.AdmissionDto;
import com.example.dto.AdmissionReportDto;
import com.example.entity.AdmissionApplication;
import com.example.entity.CourseStatus;

public interface AdmissionRepo extends JpaRepository<AdmissionApplication, Long>{

	Optional<AdmissionApplication> findBySname(String sname);

	List<AdmissionApplication> findByCourseName(String courseName);

	List<AdmissionApplication> findByEmail(String email);

	Optional<AdmissionApplication> findByEmailAndCourseName(String email, String courseName);

	@Query("SELECT COUNT (a) FROM AdmissionApplication a")
	 Long gettotalAdmission();	
	
	@Query("SELECT COUNT(a) FROM AdmissionApplication a WHERE a.courseStatus = 'IN_PROGRESS'")
	long countInProgressStudents();

	
	@Query("SELECT SUM(a.totalfree) FROM AdmissionApplication a")
	Long getTotalFees();

	@Query("SELECT SUM(a.paidfree) FROM AdmissionApplication a")
	Long getTotalPaidFee();

	@Query("SELECT SUM(a.remainingfree) FROM AdmissionApplication a")
	Long getTotalRemainingFee();


	 @Query("SELECT FUNCTION('MONTH', a.paymentdate), SUM(a.paidfree) " +
	           "FROM AdmissionApplication a " +
	           "WHERE a.paymentdate IS NOT NULL AND FUNCTION('YEAR', a.paymentdate) = :year " +
	           "GROUP BY FUNCTION('MONTH', a.paymentdate)")
	    List<Object[]> getMonthlyIncome(@Param("year") int year);
	    
	  Optional<AdmissionApplication>
	    findTopByEmailAndCourseNameOrderByIdDesc(String email, String courseName);

	  @Query("""
			    SELECT COUNT(a)
			    FROM AdmissionApplication a
			    WHERE a.paymentdate >= :startDate
			      AND a.paymentdate <= :endDate
			""")
			long countAdmissionsBetween(@Param("startDate") LocalDateTime startDate,
			                            @Param("endDate") LocalDateTime endDate);

			@Query("""
			    SELECT COALESCE(SUM(a.paidfree),0)
			    FROM AdmissionApplication a
			    WHERE a.paymentdate >= :startDate
			      AND a.paymentdate <= :endDate
			""")
			double totalPaidFeeBetween(@Param("startDate") LocalDateTime startDate,
			                           @Param("endDate") LocalDateTime endDate);


			@Query("""
					   SELECT new com.example.dto.AdmissionReportDto(
					       a.id,
					       a.sname,
					       a.email,
					       a.courseName,
					       a.createdAt
					   )
					   FROM AdmissionApplication a
					   WHERE a.createdAt BETWEEN :from AND :to
					""")
					List<AdmissionReportDto> findAdmissionReport(
					        @Param("from") LocalDate from,
					        @Param("to") LocalDate to
					);

			Optional<AdmissionApplication> findByStripeSessionId(String sessionId);
			
			 long countByEmail(String email);

		    @Query(value = "SELECT COUNT(*) FROM admission_application WHERE email = :email AND course_status = 'COMPLETED'", nativeQuery = true)
		    long countCompletedCourses(@Param("email") String email);

			AdmissionApplication findTopByEmailOrderByIdDesc(String email);
			Optional<AdmissionApplication> findTopByMobnoOrderByIdDesc(String mobno);
			    
			@Query("""
				       SELECT COALESCE(SUM(a.paidfree),0)
				       FROM AdmissionApplication a
				       WHERE FUNCTION('MONTH', a.paymentdate) = FUNCTION('MONTH', CURRENT_DATE)
				       """)
				Double getMonthlyIncome();   
			
			@Query(value = "SELECT TOP 5 * FROM admission_application ORDER BY created_at DESC", nativeQuery = true)
			List<AdmissionApplication> getRecentAdmissions();
			
			@Query(value = """
				       SELECT TOP 5 course_name, COUNT(*) as total
				       FROM admission_application
				       GROUP BY course_name
				       ORDER BY total DESC
				       """, nativeQuery = true)
				List<Object[]> getCoursePopularity();
			
				long count();

				Optional<AdmissionApplication> findByMobno(String mobno);

				Optional<AdmissionApplication> findByMobnoAndCourseName(String mobno, String courseName);

				Optional<AdmissionApplication> findTopByEmail(String email);

			

}
