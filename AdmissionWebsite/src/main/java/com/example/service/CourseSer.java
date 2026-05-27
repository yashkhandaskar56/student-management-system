package com.example.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dto.AdmissionDto;
import com.example.dto.CourseDto;
import com.example.entity.AdmissionApplication;
import com.example.entity.CourseList;
import com.example.entity.CourseStatus;
import com.example.repository.AdmissionRepo;
import com.example.repository.CourseRepo;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CourseSer {
	@Autowired
	private CourseRepo repo;
	
	
	
	@Autowired
	private AdmissionRepo arepo;
	
	public String addCourse(CourseDto dto) {
		if(dto.getCourseName().trim().isEmpty()||dto.getTotalseat()==null) {
			return "All Filed Is Required";
		}
		
		CourseList c=new CourseList();
		c.setCourseName(dto.getCourseName());
		c.setTotalseat(dto.getTotalseat());
		
		c.setCourseDurationMonths(dto.getCourseDurationMonths());
		c.setFree(dto.getFree());
		c.setDescription(dto.getDescription());
		c.setDailyHours(dto.getDailyHours());
		 int totalHours =

		    		c.getCourseDurationMonths()
		    		* 30
		    		* dto.getDailyHours();

		    		c.setTotalHours((long) totalHours);

		repo.save(c);
		
		return "Course added";
	}
	
	public List<CourseList> getAllCourse(){
		return repo.findAll();
	}
	
	public Long gettotalCourses() {
		return repo.gettotalCourses();
	}
	
	public Long getpaidCourses() {
		return repo.getpaidCourses();
	}
	
	public Long getfreeCourses() {
		return repo.getfreeCourses();
	}

	public String updateCourse(Long id, CourseDto dto) {
		// TODO Auto-generated method stub
		CourseList c = repo.findById(id)
	            .orElseThrow(() -> new RuntimeException("Course not found"));
	
	c.setCourseName(dto.getCourseName());
    c.setAvailableSeat(dto.getAvailableSeat());
    c.setTotalseat(dto.getTotalseat());
    c.setCourseDurationMonths(dto.getCourseDurationMonths());
    c.setDailyHours(dto.getDailyHours());
    
    int totalHours =

    		c.getCourseDurationMonths()
    		* 30
    		* dto.getDailyHours();

    		c.setTotalHours((long) totalHours);
    c.setFree(dto.getFree());
    
    c.setDescription(dto.getDescription());
    repo.save(c);
		return "Course Updated Successfully!";
	}

	public String deleteCourse(Long id) {
		// TODO Auto-generated method stub
		if(!repo.existsById(id)) {
			return "Course is Not Found";
		}
		
		repo.deleteById(id);
		
		return "Course is Delete";
	}
	
	public boolean isCourseComplete(String email, String courseName) {

	    AdmissionApplication app = arepo
	            .findTopByEmailAndCourseNameOrderByIdDesc(email, courseName)
	            .orElseThrow(() ->
	                    new RuntimeException("Admission not found"));

	    return CourseStatus.COMPLETED.equals(app.getCourseStatus());
	}

	
	public void markCourseCompleted(String email, String courseName) {

        AdmissionApplication app = arepo
            .findTopByEmailAndCourseNameOrderByIdDesc(email, courseName)
            .orElseThrow(() -> new RuntimeException("Admission not found"));
        
        if(app.getCourseStatus() == CourseStatus.COMPLETED){
            throw new RuntimeException("Course already completed");
        }

        app.setCourseStatus(CourseStatus.COMPLETED);
        app.setCompletionDate(LocalDate.now());

        // save() optional, but keep it
        arepo.save(app);
    }
	
	@Transactional
	public void updateCourseStatus(String email, String courseName){

	    AdmissionApplication app = arepo
	            .findTopByEmailAndCourseNameOrderByIdDesc(email, courseName)
	            .orElseThrow(() ->
	                    new RuntimeException("Student not found"));

	    // FORM_FEE_PENDING -> IN_PROGRESS
	    if(app.getCourseStatus() == CourseStatus.FORM_FEE_PENDING){

	        app.setCourseStatus(CourseStatus.IN_PROGRESS);
	    }

	    // IN_PROGRESS -> COMPLETED
	    else if(app.getCourseStatus() == CourseStatus.IN_PROGRESS){

	        app.setCourseStatus(CourseStatus.COMPLETED);
	    }

	    // COMPLETED -> IN_PROGRESS
	    else if(app.getCourseStatus() == CourseStatus.COMPLETED){

	        app.setCourseStatus(CourseStatus.IN_PROGRESS);
	    }

	    arepo.save(app);
	}





}
