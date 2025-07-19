package com.paymybuddy.service;

import com.paymybuddy.exception.EmailAlreadyExistsException;
import com.paymybuddy.exception.ResourceNotFoundException;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserServiceImpl userService;

	private User user;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setId(1L);
		user.setEmail("test@example.com");
		user.setUsername("testuser");
		user.setPassword("securepassword");
		user.setFirstName("Test");
		user.setLastName("User");
		user.setBalance(BigDecimal.valueOf(50.00));
	}

	@Test
	void getUserByEmail_WhenUserExists_ShouldReturnUser() {
		when(userRepository.findByEmail("test@example.com")).thenReturn(user);

		User result = userService.getUserByEmail("test@example.com");

		assertThat(result).isNotNull();
		assertThat(result.getEmail()).isEqualTo("test@example.com");
		verify(userRepository, times(1)).findByEmail("test@example.com");
	}

	@Test
	void getUserByEmail_WhenUserNotFound_ShouldThrowException() {
		when(userRepository.findByEmail("notfound@example.com")).thenReturn(null);

		assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail("notfound@example.com"));

		verify(userRepository).findByEmail("notfound@example.com");
	}

	@Test
	void checkEmailUniqueness_WhenEmailUsedByAnotherUser_ShouldThrow() {
		User other = new User();
		other.setId(2L);
		other.setEmail("test@example.com");

		when(userRepository.findByEmail("test@example.com")).thenReturn(other);

		assertThrows(EmailAlreadyExistsException.class, () -> userService.checkEmailUniqueness(1L, "test@example.com"));
	}

	@Test
	void checkEmailUniqueness_WhenEmailUnusedOrSameUser_ShouldPass() {
		// email used by same user (no error)
		when(userRepository.findByEmail("test@example.com")).thenReturn(user);

		assertDoesNotThrow(() -> userService.checkEmailUniqueness(1L, "test@example.com"));

		// email not used (return null)
		when(userRepository.findByEmail("new@example.com")).thenReturn(null);

		assertDoesNotThrow(() -> userService.checkEmailUniqueness(1L, "new@example.com"));
	}
}
