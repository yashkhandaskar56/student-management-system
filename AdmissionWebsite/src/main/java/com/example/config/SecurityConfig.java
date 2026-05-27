package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import com.stripe.Stripe;

import jakarta.annotation.PostConstruct;


@Configuration
public class SecurityConfig {

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf.disable())
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/Student/**").permitAll()
//                .requestMatchers("/admission/**").permitAll()
//                .requestMatchers("/course/**").permitAll()
//                .anyRequest().authenticated()
//            );
//
//        return http.build();
//    }
	
	 @Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http.csrf().disable() // disable CSRF for testing
	            .authorizeHttpRequests(auth -> auth
	                .anyRequest().permitAll() // allow all requests
	            )
	            .formLogin().disable() // disable default login page
	            .httpBasic().disable(); // disable basic auth

	        return http.build();
	    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Primary
	@Bean
	public FreeMarkerConfigurationFactoryBean factoryBean() {
		FreeMarkerConfigurationFactoryBean bean=new FreeMarkerConfigurationFactoryBean();
		bean.setTemplateLoaderPath("classpath:/templates");
		return bean;
	}
    
  
}
