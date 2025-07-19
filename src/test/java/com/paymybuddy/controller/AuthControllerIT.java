package com.paymybuddy.controller;

import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void cleanUp() {
		// Supprime l’utilisateur si déjà présent pour éviter les erreurs SQL
		User existing = userRepository.findByEmail("existing@example.com");
		if (existing != null) {
			userRepository.delete(existing);
		}
	}

	@Test
	void loginPage_ShouldReturnLoginView() throws Exception {
		mockMvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"));
	}

	@Test
	void registerPage_ShouldReturnRegisterView() throws Exception {
		mockMvc.perform(get("/register")).andExpect(status().isOk()).andExpect(view().name("register"))
				.andExpect(model().attributeExists("user"));
	}

	@Test
	void registerSave_ShouldCreateUserAndRedirect() throws Exception {
		User existing = userRepository.findByEmail("newuser@example.com");
		if (existing != null) {
			userRepository.delete(existing);
		}

		mockMvc.perform(post("/register/save").param("username", "newuser").param("email", "newuser@example.com")
				.param("password", "mypassword").with(csrf())).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?registerSuccess"));

		User savedUser = userRepository.findByEmail("newuser@example.com");
		assertThat(savedUser).isNotNull();
		assertThat(savedUser.getUsername()).isEqualTo("newuser");
		assertThat(passwordEncoder.matches("mypassword", savedUser.getPassword())).isTrue();
	}

	@Test
	void registerSave_ShouldRedirectBack_WhenEmailAlreadyExists() throws Exception {
		mockMvc.perform(post("/register/save").param("username", "existinguser").param("email", "existing@example.com")
				.param("password", "secret").with(csrf())).andExpect(status().is3xxRedirection());

		mockMvc.perform(post("/register/save").param("username", "newuser").param("email", "existing@example.com")
				.param("password", "newpassword").with(csrf())).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/register")).andExpect(flash().attributeExists("emailError"))
				.andExpect(flash().attribute("emailError", "Email déjà existant"));
	}
}
