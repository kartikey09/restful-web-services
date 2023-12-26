package com.in28minutes.rest.webservices.restfulwebservices.helloWorld;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class helloWorld {
	@GetMapping("hello-world")
	public String helloWorldfunc() {
		return "Hello World";
	}
	
	@GetMapping("hello-world-bean")
	public helloWorldBean helloWorldPOJO() {
		return new helloWorldBean("Hello World Bean");
	}
}
