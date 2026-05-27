package com.example.controller;


import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import com.example.entity.CourseMaterial;
import com.example.service.CourseMaterialSer;

import org.springframework.core.io.Resource;



@RestController
@RequestMapping("/Course-Material")
public class CourseMaterialController {
	
	@Autowired
	private CourseMaterialSer ser;
	
	@PostMapping("/upload")
	public ResponseEntity<?> uploadMaterial(
			@RequestParam("courseName") String courseName,@RequestParam("title") String title,
			@RequestParam("description") String desc,
			@RequestParam("file") MultipartFile file) throws IllegalStateException, IOException{
		
		CourseMaterial saved=ser.uploadMaterial(courseName, title, desc, file);
		
		return ResponseEntity.ok(saved);
	}
	
	@GetMapping("/All")
	public List<CourseMaterial> getAllMaterial(){
		return ser.getAllMaterial();
	}
	
	// VIEW (Open in browser)
	@GetMapping("/view/id/{id}")
	public ResponseEntity<Resource> viewFile(@PathVariable Long id) throws IOException {

	    Resource resource = ser.loadFile(id);

	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION,
	                    "inline; filename=\"" + resource.getFilename() + "\"")
	            .contentType(MediaType.APPLICATION_PDF)
	            .body(resource);
	}


	// DOWNLOAD (Force download)
	@GetMapping("/download/id/{id}")
	public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws IOException {

	    Resource resource = ser.loadFile(id);

	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION,
	                    "attachment; filename=\"" + resource.getFilename() + "\"")
	            .contentType(MediaType.APPLICATION_OCTET_STREAM)
	            .body(resource);
	}


	
	
	
	

}
