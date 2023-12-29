package com.in28minutes.rest.webservices.restfulwebservices.user;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

@RestController
public class UserResource {
	UserDaoService service;

	@Autowired
	public UserResource(UserDaoService service) {
		this.service = service;
	}

	@GetMapping(path = "/users")
	public List<User> retrieveAllUsers() {
		return service.findAll();
	}

	@GetMapping(path = "/users/{id}")
	public EntityModel<User> retrieveUserById(@PathVariable Integer id) {
		User user = service.findUserById(id);
		if (user == null) {
			throw new UserNotFoundException("id: " + id);
		}
		
		EntityModel<User> entityModel = EntityModel.of(user); //here we are Wrapping the user in an entity model so that we can add HATEOAS to our https messages;
		WebMvcLinkBuilder link = linkTo(methodOn(this.getClass()).retrieveAllUsers());
		entityModel.add(link.withRel("all-users"));
		return entityModel;
	}

	@DeleteMapping(path = "/users/{id}")
	public void deleteUserById(@PathVariable Integer id) {
		User user = service.findUserById(id);
		if (user == null) {
			throw new UserNotFoundException("id: " + id);
		}
		service.deleteById(id);
	}

	@PostMapping("/users")
	public ResponseEntity<User> addUser(@Valid @RequestBody User user) { // the user body that comes in is mapped to the User
																	// bean and is passed in the function parameter as
																	// an object
		User savedUser = service.save(user);
		System.out.println(ServletUriComponentsBuilder.fromCurrentRequest().toString());
		URI location = ServletUriComponentsBuilder.fromCurrentRequest() // the whole current path as it is
				.path("/{id}") // appending /{id} to the current path
				.buildAndExpand(savedUser.getId()) // adding the original id in place of "{id}" and building the path
				.toUri(); // finally converting to uri
		return ResponseEntity.created(location).build(); // ResponseEntity class helps use to respond back with status
															// codes
	}
}
