package com.in28minutes.rest.webservices.restfulwebservices.security;

import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfiguration {
	@Bean
	public SecurityFilterChain filterchain(HttpSecurity http) throws Exception {
		
		//authenticate all requests
		http.authorizeHttpRequests(auth->auth.anyRequest().authenticated());
		
		//if a request is not authenticated, a web page is shown
		http.httpBasic(withDefaults());
		
		//disabling CSRF for Put and Delete methods
		http.csrf(csrf->csrf.disable());
				
		return http.build();
	}
	
}
