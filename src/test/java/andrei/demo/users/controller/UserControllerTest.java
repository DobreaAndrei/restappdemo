package andrei.demo.users.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import andrei.demo.users.entity.User;
import andrei.demo.users.exceptions.UserDoesNotExistException;
import andrei.demo.users.exceptions.UserIdMissmatchException;
import andrei.demo.users.service.UserService;

@ExtendWith(SpringExtension.class)
class UserControllerTest {

	@Mock
	private UserService userService;

	@InjectMocks
	private UserController userController;

	private MockMvc mockMvc;
	private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final String ID = UUID.randomUUID().toString();
	private static final String WRONG_ID = UUID.randomUUID().toString();

	private static final User USER = User.builder().id(ID).name("Andrei").build();

	@BeforeEach
	private void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
	}

	@Test
	public void shouldReturn200ForExistingId() throws Exception {
		Mockito.when(userService.getUser(ID)).thenReturn(USER);

		MvcResult mvcResult = mockMvc.perform(get("/users/" + ID)).andExpect(status().isOk()).andReturn();

		User response = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(), User.class);

		Assertions.assertThat(response).isEqualTo(USER);
	}

	@Test
	public void shouldReturn404ForUnknownId() throws Exception {
		Mockito.when(userService.getUser(WRONG_ID)).thenThrow(new UserDoesNotExistException());

		mockMvc.perform(get("/users/" + WRONG_ID)).andExpect(status().isNotFound());
	}

	@Test
	public void shouldReturn200ForCreatingUser() throws Exception {
		final String userName = "Alex";

		final User newUser = new User(userName);

		final User savedUser = new User(UUID.randomUUID().toString(), userName);

		Mockito.when(userService.addUser(newUser)).thenReturn(savedUser);

		MvcResult mvcResult = mockMvc.perform(post("/users/").contentType(MediaType.APPLICATION_JSON)
				.content(OBJECT_MAPPER.writeValueAsString(newUser))).andExpect(status().isOk()).andReturn();

		User response = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(), User.class);

		Assertions.assertThat(response).isEqualTo(savedUser);
	}

	@Test
	public void shouldReturn200ForUpdatingUser() throws Exception {
		final User userToUpdate = new User(UUID.randomUUID().toString(), "Dan");

		Mockito.when(userService.updateUser(userToUpdate.getId(), userToUpdate)).thenReturn(userToUpdate);

		MvcResult mvcResult = mockMvc
				.perform(put("/users/" + userToUpdate.getId()).contentType(MediaType.APPLICATION_JSON)
						.content(OBJECT_MAPPER.writeValueAsString(userToUpdate)))
				.andExpect(status().isOk()).andReturn();

		User response = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(), User.class);

		Assertions.assertThat(response).isEqualTo(userToUpdate);
	}

	@Test
	public void shouldReturn400ForIdMissmatch() throws Exception {
		Mockito.when(userService.updateUser(WRONG_ID, USER)).thenThrow(new UserIdMissmatchException());

		mockMvc.perform(put("/users/" + WRONG_ID).contentType(MediaType.APPLICATION_JSON)
				.content(OBJECT_MAPPER.writeValueAsString(USER))).andExpect(status().isBadRequest());

	}
	
	@Test
	public void shouldReturn404ForUpdatingNonExistingId() throws Exception {
		final User userToUpdate = new User(WRONG_ID, "Dan");
		
		Mockito.when(userService.updateUser(WRONG_ID, userToUpdate)).thenThrow(new UserDoesNotExistException());

		mockMvc.perform(put("/users/" + WRONG_ID).contentType(MediaType.APPLICATION_JSON)
				.content(OBJECT_MAPPER.writeValueAsString(userToUpdate))).andExpect(status().isNotFound());

	}

	@Test
	public void shouldReturn200ForDeletingUser() throws Exception {
		Mockito.when(userService.deleteUser(ID)).thenReturn(USER);

		MvcResult mvcResult = mockMvc.perform(delete("/users/" + ID)).andExpect(status().isOk()).andReturn();

		User response = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(), User.class);

		Assertions.assertThat(response).isEqualTo(USER);

	}

	@Test
	public void shouldReturn404ForDeletingWrongIdUser() throws Exception {
		Mockito.when(userService.deleteUser(WRONG_ID)).thenThrow(new UserDoesNotExistException());

		mockMvc.perform(delete("/users/" + WRONG_ID)).andExpect(status().isNotFound());

	}
	
	@Test
	public void shouldReturn200ForGetUsers() throws Exception {
		final List<User> users = Arrays.asList(USER);
		
		Mockito.when(userService.getUsers()).thenReturn(users);

		MvcResult mvcResult = mockMvc.perform(get("/users/")).andExpect(status().isOk()).andReturn();

		List<User> response = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<User>>(){});

		Assertions.assertThat(response.size()).isEqualTo(1);
		Assertions.assertThat(response.get(0)).isEqualTo(USER);
	}
	
	

}