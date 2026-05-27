package com.example.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.entity.AdminEntity;
import com.example.entity.Role;
import com.example.repository.AdminRepo;

@Service
public class AdminSer {
	
	@Autowired
	private AdminRepo adminRepo;
	
	@Autowired
	private PasswordEncoder encoder;
	
	public String adminRegister(AdminEntity a) {
		a.setAdminName(a.getAdminName());
		a.setEmail(a.getEmail());
		a.setPassword(encoder.encode(a.getPassword()));
		a.setRole(Role.ADMIN);
		
		adminRepo.save(a);
		
		return "Admin Register Successfully";
		
	}

	 public Map<String, String> adminLogin(AdminEntity a) {

	        if (a.getEmail() == null || a.getPassword() == null) {
	            return Map.of("message", "All fields are required!");
	        }

	        Optional<AdminEntity> adminOpt = adminRepo.findByEmail(a.getEmail());

	        if (adminOpt.isEmpty()) {
	            return Map.of("message", "Admin not found!");
	        }

	        AdminEntity admin = adminOpt.get();

	        if (!encoder.matches(a.getPassword(), admin.getPassword())) {
	            return Map.of("message", "Invalid password!");
	        }

	        // SUCCESS
	        return Map.of(
	                "message", "success",
	                "role", "ADMIN",
	                "adminName", admin.getAdminName()
	        );
	    }
	 
	 
	 public String forgotPassword(String email,String password){

		    Optional<AdminEntity> adminOpt = adminRepo.findByEmail(email);

		    if(adminOpt.isEmpty()){
		        return "Email not found";
		    }

		    AdminEntity admin = adminOpt.get();

		    // encode password
		    admin.setPassword(encoder.encode(password));

		    adminRepo.save(admin);

		    return "Password updated successfully";
		}

}
