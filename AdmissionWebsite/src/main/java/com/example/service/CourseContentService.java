package com.example.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.CourseContent;
import com.example.repository.CourseContentRepository;


@Service
public class CourseContentService {
	
	  @Autowired
	  
	    private CourseContentRepository repo;

	    public CourseContent addContent(CourseContent content){

	        content.setUploadDate(LocalDate.now());

	        return repo.save(content);
	    }

	    public List<CourseContent> getLatestContent(){

	        return repo.findTop5ByOrderByUploadDateDesc();
	    }

}
