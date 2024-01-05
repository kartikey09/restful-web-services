package com.in28minutes.rest.webservices.restfulwebservices.user;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

import com.in28minutes.rest.webservices.restfulwebservices.jpa.postRepository;
import com.in28minutes.rest.webservices.restfulwebservices.jpa.userRepository;

import jakarta.validation.Valid;

@RestController
public class UserJpaResource {
	userRepository repository;
	postRepository postRepository;

	@Autowired
	public UserJpaResource(userRepository repository, postRepository postRepository) {
		this.repository = repository;
		this.postRepository = postRepository;
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

	@GetMapping(path = "/jpa/users/{id}/posts")
	public List<Post> retrievePostsForUser(@PathVariable Integer id) {
		Optional<User> user = repository.findById(id);
		if (user.isEmpty()) {
			throw new UserNotFoundException("User with id: " + id + " does not exist");
		}
		return user.get().getPosts();
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

	@PostMapping(path = "/jpa/users/{id}/posts")
	public ResponseEntity<Object> createPostForUser(@PathVariable Integer id, @Valid @RequestBody Post post) {
		Optional<User> user = repository.findById(id);	
		if (user == null) {
			throw new UserNotFoundException("User with id: " + id + " does not exist");
		}

		post.setUser(user.get());
		Post savedPost = postRepository.save(post);
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedPost.getId())
				.toUri();

		return ResponseEntity.created(location).build();

	}
	
	@GetMapping(path = "/jpa/users/{uid}/posts/{pid}")
	public Post getPostByUser(@PathVariable Integer uid, @PathVariable Integer pid) {
		Optional<User> user = repository.findById(uid);
		if(user.isEmpty()) {
			throw new UserNotFoundException("User with id: " + uid + " does not exist");
		}
		
		Optional<Post> post = postRepository.findById(pid);
		if(post.isEmpty()) {
			throw new PostNotFoundException("User with id: " + uid + " does not exist");
		}
		
		return post.get();
		
	}
	
}

/*
 * In the context of Spring Boot, there's no direct comparison between
 * "ResponseEntity" and "EntityModel" because they serve different purposes.
 * 
 * 1. **ResponseEntity:** - `ResponseEntity` is a class provided by Spring
 * Framework to represent the entire HTTP response, including status code,
 * headers, and body. - It is commonly used in controller methods to have more
 * control over the HTTP response, allowing you to customize the status code,
 * headers, and response body. - Used for building and customizing the response
 * returned from your RESTful web services.
 * 
 * Example: ```java
 * 
 * @GetMapping("/example") public ResponseEntity<String> example() { // business logic 
 * 		return ResponseEntity.status(HttpStatus.OK).body("Success"); } ```
 * 
 * 2. **EntityModel:** - `EntityModel` is part of the Spring HATEOAS module and
 * is used for representing a single resource along with related links. It is
 * often used in the context of hypermedia-driven RESTful APIs. - It wraps your
 * domain object (entity) and adds hypermedia links to it, providing additional
 * information about how to navigate to related resources. - Helps in building
 * responses that follow HATEOAS principles, making your API more discoverable.
 * 
 * Example: ```java
 * 
 * @GetMapping("/example/{id}") public EntityModel<EntityType>
 * getEntity(@PathVariable Long id) { // business logic to retrieve entity
 * EntityType entity = // retrieve entity based on id return
 * EntityModel.of(entity,
 * linkTo(methodOn(YourController.class).getEntity(id)).withSelfRel(), //
 * additional links ); } ```
 * 
 * In summary, `ResponseEntity` is more about controlling the overall HTTP
 * response, including status and headers, while `EntityModel` is specifically
 * designed for building responses that adhere to HATEOAS principles by
 * including hypermedia links with your domain objects. Depending on your use
 * case and REST API design principles, you may use them in conjunction to
 * achieve the desired behavior in your Spring Boot application.
 */
