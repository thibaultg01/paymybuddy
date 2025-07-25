package com.paymybuddy.service;

import com.paymybuddy.model.User;
import java.util.List;

public interface UserService {
	User createUser(User user);

	User updateUser(Long id, User updatedUser);

	User getUserByEmail(String email);

	void checkEmailUniqueness(Long userId, String email);
}
