package com.example.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.entity.CourseMaterial;
import com.example.repository.CourseMaterialRepo;

import org.springframework.core.io.Resource;



@Service
public class CourseMaterialSer {
	
	@Autowired
	private CourseMaterialRepo repo;
	
	String uploadDir="C:\\Users\\DELL\\OneDrive\\Desktop\\CourseMaterial\\";
	
	public CourseMaterial uploadMaterial(String courseName, String title, String desc, MultipartFile file) throws IllegalStateException, IOException {
		
		File folder=new File(uploadDir);
		
		if(!folder.exists()) {
			folder.mkdir();
		}
		
		// Generate unique safe filename
		String originalName = file.getOriginalFilename();
		String safeName = System.currentTimeMillis() + "_" +
		                originalName.replace("\\", "_").replace("/", "_");

		File filepath = new File(uploadDir + safeName);
		file.transferTo(filepath);

		CourseMaterial c = new CourseMaterial();
		c.setCourseName(courseName);
		c.setTitle(title);
		c.setDescription(desc);
		c.setFilename(safeName);      // <-- the real file name
		c.setFilepath(safeName);      // <-- only filename
		c.setUploadedAt(LocalDateTime.now());


		
		return repo.save(c);
		
	}

	public List<CourseMaterial> getAllMaterial() {
		// TODO Auto-generated method stub
		return repo.findAll();
	}
	
	public List<CourseMaterial> getByCourseName(String courseName){
		return repo.findByCourseName(courseName);
		
	}
	
	public CourseMaterial getById(Long id) {
		return repo.findById(id).orElseThrow(()-> new RuntimeException("Material Not Found For This Id"));
	}
	
	

	public Resource loadFile(Long id) throws IOException {

	    CourseMaterial material = repo.findById(id)
	            .orElseThrow(() -> new RuntimeException("Material not found"));

	    File file = new File(uploadDir + material.getFilename());

	    if (!file.exists()) {
	        throw new RuntimeException("File not found at: " + file.getAbsolutePath());
	    }

	    return new UrlResource(file.toURI());
	}





}
