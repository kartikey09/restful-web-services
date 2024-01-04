package com.in28minutes.rest.webservices.restfulwebservices.user;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.net.URI;
import java.util.List;
import java.util.Optional;

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

import com.in28minutes.rest.webservices.restfulwebservices.jpa.userRepository;

import jakarta.validation.Valid;

@RestController
public class UserJpaResource {
	userRepository repository;

	@Autowired
	public UserJpaResource(userRepository repository) {
		this.repository = repository;
	}

	@GetMapping(path = "/jpa/users")
	public List<User> retrieveAllUsers() {
		return repository.findAll();
	}

	@GetMapping(path = "/jpa/users/{id}")
	public EntityModel<User> retrieveUser(@PathVariable Integer id) {
		Optional<User> user = repository.findById(id);
		if (user.isEmpty()) {
			throw new UserNotFoundException("User with id: " + id + " does not exist");
		}

		EntityModel<User> entityModel = EntityModel.of(user.get()); // here we are Wrapping the user in an entity model
																	// so that we can add HATEOAS to our https messages
																	// the user.get() method gets use the user object
																	// that is wrapped up in optional object
		WebMvcLinkBuilder link = linkTo(methodOn(this.getClass()).retrieveAllUsers());
		entityModel.add(link.withRel("all-users"));
		return entityModel;
	}

	@DeleteMapping(path = "/jpa/users/{id}")
	public void deleteUserById(@PathVariable Integer id) {
		Optional<User> user = repository.findById(id);
		if (user.isEmpty()) {
			throw new UserNotFoundException("User with id: " + id + " does not exist");
		}
		repository.deleteById(id);
	}

	@PostMapping("/jpa/users")
	public ResponseEntity<User> addUser(@Valid @RequestBody User user) {// the user body that comes in is mapped to the
																		// User bean and is passed in the function
																		// parameter as an object
		User savedUser = repository.save(user);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest() // the whole current path as it is
				.path("/{id}") // appending /{id} to the current path
				.buildAndExpand(savedUser.getId()) // adding the original id in place of "{id}" and building the path
				.toUri(); // finally converting to uri
		return ResponseEntity.created(location).build(); // ResponseEntity class helps use to respond back with status
															// codes
	}
}
