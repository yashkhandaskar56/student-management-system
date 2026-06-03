package com.example.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.AdminEntity;
import com.example.service.AdminSer;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminSer ser;
	
	@PostMapping("/register")
	public String AdminRegister(@RequestBody AdminEntity a) {
		return ser.adminRegister(a);
	}
	
	 @PostMapping("/login")
	    public Map<String, String> login(@RequestBody AdminEntity a) {
	        return ser.adminLogin(a);
	    }
	
	 @PostMapping("/forgot-password")
	 public String forgotPassword(@RequestBody Map<String,String> data){

	     String email = data.get("email");
	     String password = data.get("password");

	     return ser.forgotPassword(email,password);
	 }
}
