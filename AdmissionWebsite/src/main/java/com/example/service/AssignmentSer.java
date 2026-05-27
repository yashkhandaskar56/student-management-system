package com.example.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.entity.Assignment;
import com.example.repository.AssignmentRepo;

@Service
public class AssignmentSer {
	
	@Autowired
	private AssignmentRepo repo;
	
	@Autowired
	private ActivityService activityService;
	
	
	// Admin: Create assignment
	public Assignment createAssignment(Assignment a) {
		a.setStatus("PENDING");
		return repo.save(a);
	}
	
	public List<Assignment> getAssignmentByStudent(String email){
		return repo.findByEmail(email);
	}

	public Assignment submitAssignmnet(Long id, String submissionText, MultipartFile file)
	        throws IllegalStateException, IOException {

	    Assignment a = repo.findById(id)
	            .orElseThrow(() -> new RuntimeException("Assignment Not Found"));

	    a.setSubmissionText(submissionText);

	    if (file != null && !file.isEmpty()) {

	        String folder = "C:\\Users\\DELL\\OneDrive\\Desktop\\Assignmnet";
	        File dir = new File(folder);

	        if (!dir.exists())
	            dir.mkdir();

	        String filePath = folder + file.getOriginalFilename();

	        file.transferTo(new File(filePath));

	        a.setAttachmentUrl(filePath);
	    }

	    a.setStatus("SUBMITTED");

	    Assignment saved = repo.save(a);

	    // ✅ RECENT ACTIVITY SAVE
	    activityService.saveActivity(a.getEmail(), "Assignment Uploaded");

	    return saved;
	}

}
