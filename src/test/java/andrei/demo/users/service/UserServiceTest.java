package andrei.demo.users.service;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import andrei.demo.users.entity.User;
import andrei.demo.users.exceptions.UserDoesNotExistException;
import andrei.demo.users.exceptions.UserIdMissmatchException;
import andrei.demo.users.repository.UserRepository;

@ExtendWith(SpringExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	private static final String ID = UUID.randomUUID().toString();
	private static final String WRONG_ID = UUID.randomUUID().toString();
	private static User USER;

	@BeforeEach
	private void setUp() {
		User.UserBuilder USER_BUILDER = User.builder().id(ID).name("Andrei");
		USER = USER_BUILDER.build();
	}

	@Test
	public void shouldReturnExistingUser() throws Exception {
		Mockito.when(userRepository.findById(ID)).thenReturn(Optional.of(USER));

		User user = userService.getUser(ID);

		Assertions.assertThat(user).isEqualTo(USER);
	}

	@Test
	public void shouldReturnEmptyWhenUserDoesNotExist() throws Exception {
		when(userRepository.findById(WRONG_ID)).thenReturn(Optional.empty());

		Assertions.assertThatThrownBy(() -> userService.getUser(WRONG_ID))
				.isExactlyInstanceOf(UserDoesNotExistException.class);
	}

	@Test
	public void shouldUpdateExistingUser() throws Exception {
		Mockito.when(userRepository.findById(ID)).thenReturn(Optional.of(USER));
		Mockito.when(userRepository.save(USER)).thenReturn(USER);

		User user = userService.updateUser(USER.getId(), USER);

		Assertions.assertThat(user).isEqualTo(USER);
	}

	@Test
	public void shouldReturnMissmatchIdException() throws Exception {
		Mockito.when(userRepository.save(USER)).thenReturn(USER);

		Assertions.assertThatThrownBy(() -> userService.updateUser(UUID.randomUUID().toString(), USER))
				.isExactlyInstanceOf(UserIdMissmatchException.class);
	}

	@Test
	public void shouldReturnUserDoesNotExistException() throws Exception {
		final User wrongUser = new User(WRONG_ID, "Alex");
		Mockito.when(userRepository.findById(WRONG_ID)).thenReturn(Optional.empty());

		Assertions.assertThatThrownBy(() -> userService.updateUser(WRONG_ID, wrongUser))
				.isExactlyInstanceOf(UserDoesNotExistException.class);
	}

	@Test
	public void shouldReturnUserList() throws Exception {
		final List<User> users = Arrays.asList(USER);

		Mockito.when(userRepository.findAll()).thenReturn(users);

		List<User> persistedUsers = userService.getUsers();

		Assertions.assertThat(persistedUsers.size()).isEqualTo(users.size());
		Assertions.assertThat(persistedUsers.get(0)).isEqualTo(users.get(0));

	}

	@Test
	public void shouldDeleteUser() throws Exception {
		Mockito.when(userRepository.findById(ID)).thenReturn(Optional.of(USER)).thenReturn(Optional.empty());

		userService.deleteUser(ID);

		Mockito.verify(userRepository, VerificationModeFactory.times(1)).delete(USER);
		
		Assertions.assertThatThrownBy(() -> userService.getUser(ID))
		.isExactlyInstanceOf(UserDoesNotExistException.class);

	}
	
	@Test
	public void shouldReturnUserDoesNotExistExceptionOnDeleteUser() throws Exception {
		Mockito.when(userRepository.findById(WRONG_ID)).thenReturn(Optional.empty());

		Assertions.assertThatThrownBy(() -> userService.deleteUser(ID))
		.isExactlyInstanceOf(UserDoesNotExistException.class);

	}

}