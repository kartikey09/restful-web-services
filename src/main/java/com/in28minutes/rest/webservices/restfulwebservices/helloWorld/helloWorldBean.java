package com.in28minutes.rest.webservices.restfulwebservices.helloWorld;

public class helloWorldBean {
	String message;

	public helloWorldBean(String message) {
		super();
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "helloWorldBean [message=" + message + "]";
	}	
}
