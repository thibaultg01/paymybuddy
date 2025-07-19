package com.paymybuddy.security;

import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private CustomUserDetailsService service;

	@Test
	void testLoadUserByUsername_UserFound() {
		String email = "test@example.com";
		User user = new User();
		user.setEmail(email);
		user.setPassword("pass");

		when(userRepository.findByEmail(email)).thenReturn(user);

		UserDetails result = service.loadUserByUsername(email);
		assertEquals(email, result.getUsername());
		assertEquals("pass", result.getPassword());
	}

	@Test
	void testLoadUserByUsername_NotFound() {
		when(userRepository.findByEmail("missing@example.com")).thenReturn(null);

		assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("missing@example.com"));
	}
}
