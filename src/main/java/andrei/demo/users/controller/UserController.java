package andrei.demo.users.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import andrei.demo.users.entity.User;
import andrei.demo.users.exceptions.UserDoesNotExistException;
import andrei.demo.users.exceptions.UserIdMissmatchException;
import andrei.demo.users.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	private UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@ExceptionHandler(UserDoesNotExistException.class)
	public ResponseEntity<?> handleUserDoesNotExistException(UserDoesNotExistException ex) {
		return new ResponseEntity<>("User does not exist!", HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(UserIdMissmatchException.class)
	public ResponseEntity<?> handleUserIdMissmatchException(UserIdMissmatchException ex) {
		return new ResponseEntity<>("User id and pathId do not match!", HttpStatus.BAD_REQUEST);
	}

	@GetMapping("/")
	public ResponseEntity<List<User>> getAllUsers() {
		return new ResponseEntity<List<User>>(userService.getUsers(), HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<User> getUser(@PathVariable String id) throws Exception {
		return new ResponseEntity<User>(userService.getUser(id), HttpStatus.OK);
	}

	@PostMapping("/")
	public ResponseEntity<User> addUser(@RequestBody User user) throws Exception {
		return new ResponseEntity<User>(userService.addUser(user), HttpStatus.OK);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) throws Exception {
		return new ResponseEntity<User>(userService.updateUser(id, user), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<User> deleteUser(@PathVariable String id) throws Exception {
		return new ResponseEntity<User>(userService.deleteUser(id), HttpStatus.OK);
	}
}
