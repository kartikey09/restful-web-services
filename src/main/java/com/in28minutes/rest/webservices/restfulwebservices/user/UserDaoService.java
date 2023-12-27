package com.in28minutes.rest.webservices.restfulwebservices.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;

@Component
public class UserDaoService {
	private static List<User> users = new ArrayList<>();
	private static int userCount = 0;
	static {
		users.add(new User(++userCount, "Adam", LocalDate.now().minusYears(30)));
		users.add(new User(++userCount, "Eve", LocalDate.now().minusYears(35)));
		users.add(new User(++userCount, "Jim", LocalDate.now().minusYears(20)));
	}
	
	public User save(User user) {
		user.setId(++userCount);
		users.add(user);
		return user;
	}
	
	public List<User> findAll(){
		return users;
	}

	public User findUserById(Integer id) {
		Predicate<? super User> predicate = p -> p.getId().equals(id);
		return users.stream().filter(predicate).findFirst().orElse(null);
	}
	
	
	
}
