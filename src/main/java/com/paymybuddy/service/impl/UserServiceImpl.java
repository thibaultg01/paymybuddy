package com.paymybuddy.service.impl;

import com.paymybuddy.exception.EmailAlreadyExistsException;
import com.paymybuddy.exception.ResourceNotFoundException;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.security.CustomUserDetails;
import com.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public List<User> getAllUsers() {
		logger.debug("Récupération de tous les utilisateurs");
		return userRepository.findAll();
	}

	@Override
	public User getUserById(Long id) {
		logger.debug("Recherche de l'utilisateur avec l'ID : {}", id);
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id : " + id));
	}

	@Override
	public User getUserByEmail(String email) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new ResourceNotFoundException("Utilisateur non trouvé avec l'email : " + email);
		}
		return user;
	}

	@Override
	public User createUser(User user) {
		logger.debug("createUser avec l'email : {}", user.getEmail());
		if (userRepository.findByEmail(user.getEmail()) != null) {
			throw new EmailAlreadyExistsException("Email déjà existant");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		logger.info("Nouvel utilisateur sauvegardé avec succès");
		return userRepository.save(user);
	}

	@Override
	public User updateUser(Long id, User updatedUser) {
	    User existingUser = userRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id : " + id));

	    User userWithEmail = userRepository.findByEmail(updatedUser.getEmail());
	    if (userWithEmail != null && !userWithEmail.getId().equals(id)) {
	        throw new EmailAlreadyExistsException("Email déjà existant");
	    }

	    existingUser.setEmail(updatedUser.getEmail());
	    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
	    existingUser.setUsername(updatedUser.getUsername());
	    existingUser.setFirstName(updatedUser.getFirstName());
	    existingUser.setLastName(updatedUser.getLastName());
	    existingUser.setBalance(updatedUser.getBalance());

	    User savedUser = userRepository.save(existingUser);

	    // ✅ Re-authentification après modification de l'email
	    UserDetails userDetails = new CustomUserDetails(savedUser); // ou UserPrincipal
	    Authentication newAuth = new UsernamePasswordAuthenticationToken(
	            userDetails,
	            userDetails.getPassword(),
	            userDetails.getAuthorities()
	    );
	    SecurityContextHolder.getContext().setAuthentication(newAuth);

	    return savedUser;
	}
	
	@Override
	public void checkEmailUniqueness(Long userId, String email) {
	    User existingUser = userRepository.findByEmail(email);
	    if (existingUser != null && !existingUser.getId().equals(userId)) {
	        throw new EmailAlreadyExistsException("Email déjà existant");
	    }
	}
}