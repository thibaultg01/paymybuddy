package com.paymybuddy.controller;

import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(UserWebController.class)
class UserWebControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	private User mockUser;

	@BeforeEach
	void setup() {
		mockUser = new User();
		mockUser.setId(1L);
		mockUser.setEmail("user@example.com");
		mockUser.setUsername("yolo45");
		mockUser.setPassword("password");
		mockUser.setBalance(BigDecimal.valueOf(50));
	}

	@Test
	@WithMockUser(username = "user@example.com")
	void showProfile_ReturnsProfileView() throws Exception {
		given(userService.getUserByEmail("user@example.com")).willReturn(mockUser);

		mockMvc.perform(get("/profile")).andExpect(status().isOk()).andExpect(view().name("profile"))
				.andExpect(model().attributeExists("user"));
	}

	@Test
	@WithMockUser(username = "user@example.com")
	void showEditProfileForm_ReturnsEditProfileView() throws Exception {
		given(userService.getUserByEmail("user@example.com")).willReturn(mockUser);

		mockMvc.perform(get("/profile/edit")).andExpect(status().isOk()).andExpect(view().name("edit-profile"))
				.andExpect(model().attributeExists("user"));
	}

	@Test
	@WithMockUser(username = "user@example.com")
	void updateProfile_WithValidData_RedirectsToProfile() throws Exception {
		given(userService.getUserByEmail("user@example.com")).willReturn(mockUser);
		willDoNothing().given(userService).checkEmailUniqueness(any(), any());
		given(userService.updateUser(eq(1L), ArgumentMatchers.any(User.class))).willReturn(mockUser);

		mockMvc.perform(post("/profile/edit").param("username", "updatedUser").param("email", "user@example.com")
				.param("password", "newpassword").with(csrf())).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/profile"));
	}

}
