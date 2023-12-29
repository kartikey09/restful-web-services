package com.in28minutes.rest.webservices.restfulwebservices.helloWorld;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

//RestController by default has the @ResponseBody annotation how ever with @Controller we will have to attach @ResponseBody else a string returning func will return a view 
@RestController
public class helloWorld {
	
	private MessageSource messageSource;
	
	@Autowired
	public helloWorld(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@GetMapping("hello-world")
	public String helloWorldfunc() {
		return "Hello World";
	}
	
	@GetMapping("hello-world-i18n")
	public String helloWorldfunci18n() {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage("good.morning.message", null, "Default message", locale );
	}
	
	@GetMapping("hello-world-bean")
	public helloWorldBean helloWorldPOJO() {
		return new helloWorldBean("Hello World Bean");
	}
	
	//Path variables and reqParams are different !
	@GetMapping("hello-world-bean/path-variable/{name}")
	public helloWorldBean helloWorldPathVariable(@PathVariable String name) {
		return new helloWorldBean(String.format("Hello world, %s", name));
	}
}
