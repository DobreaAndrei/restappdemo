package andrei.demo.users.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import andrei.demo.users.entity.User;
import andrei.demo.users.exceptions.UserDoesNotExistException;
import andrei.demo.users.exceptions.UserIdMissmatchException;
import andrei.demo.users.repository.UserRepository;

@Service
public class UserService {

	private UserRepository repository;

	@Autowired
	public UserService(UserRepository repository) {
		this.repository = repository;
	}

	public User getUser(String userId) throws Exception {
		return this.repository.findById(userId).orElseThrow(() -> new UserDoesNotExistException());
	}

	public User addUser(User user) {
		return this.repository.save(user);
	}

	public User updateUser(String userId, User user) throws Exception {
		if (user.getId() != userId)
			throw new UserIdMissmatchException();
		return this.repository.findById(userId).map(currentUser -> this.repository.save(user))
				.orElseThrow(() -> new UserDoesNotExistException());
	}

	public User deleteUser(String userId) throws Exception {
		User user = this.repository.findById(userId).orElseThrow(() -> new UserDoesNotExistException());
		this.repository.delete(user);
		return user;
	}

	public List<User> getUsers() {
		return this.repository.findAll();
	}

}
