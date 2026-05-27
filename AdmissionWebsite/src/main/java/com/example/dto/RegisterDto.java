package com.example.dto;

import com.example.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {
	
	private String sname;
	private String email;
	private String password;
	private Role role;

}
