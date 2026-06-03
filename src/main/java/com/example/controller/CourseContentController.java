package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.CourseContent;
import com.example.service.CourseContentService;

@RestController
@RequestMapping("/content")
@CrossOrigin("*")
public class CourseContentController {
	
	 @Autowired
	    private CourseContentService service;

	    @PostMapping("/add")
	    public CourseContent addContent(@RequestBody
	    		CourseContent content){

	        return service.addContent(content);
	    }

	    @GetMapping("/latest")
	    public List<CourseContent> getLatestContent(){

	        return service.getLatestContent();
	    }
	
	

}
