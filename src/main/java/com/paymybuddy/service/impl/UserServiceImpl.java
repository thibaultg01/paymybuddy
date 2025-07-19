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

/**
 * Implémentation du service utilisateur pour l'application Pay My Buddy.
 *
 * Cette classe gère : - la récupération d’un utilisateur par email - la
 * création d’un nouvel utilisateur avec encodage du mot de passe - la mise à
 * jour d’un utilisateur existant avec re-authentification - la vérification
 * d’unicité de l’adresse email
 */
@Service
public class UserServiceImpl implements UserService {

	private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Récupère un utilisateur à partir de son adresse email.
	 *
	 * @param email l'adresse email recherchée
	 * @return l'utilisateur correspondant
	 * @throws ResourceNotFoundException si aucun utilisateur n'est trouvé
	 */
	@Override
	public User getUserByEmail(String email) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new ResourceNotFoundException("Utilisateur non trouvé avec l'email : " + email);
		}
		return user;
	}

	/**
	 * Crée un nouvel utilisateur dans la base de données après vérification de
	 * l’unicité de l’email et encodage du mot de passe.
	 *
	 * @param user l'utilisateur à créer
	 * @return l'utilisateur enregistré
	 * @throws EmailAlreadyExistsException si l'email est déjà utilisé
	 */
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

	/**
	 * Met à jour les informations d’un utilisateur existant, avec encodage du
	 * nouveau mot de passe et vérification de l’unicité de l’email. Réinitialise
	 * l’authentification avec les nouvelles informations.
	 *
	 * @param id          l'identifiant de l'utilisateur à mettre à jour
	 * @param updatedUser les nouvelles informations de l'utilisateur
	 * @return l'utilisateur mis à jour et sauvegardé
	 * @throws ResourceNotFoundException   si l'utilisateur n'existe pas
	 * @throws EmailAlreadyExistsException si l'email est déjà utilisé par un autre
	 *                                     utilisateur
	 */
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

		UserDetails userDetails = new CustomUserDetails(savedUser);
		Authentication newAuth = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
				userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(newAuth);

		return savedUser;
	}

	/**
	 * Vérifie que l'adresse email est unique, en tenant compte de l'utilisateur
	 * courant.
	 *
	 * @param userId l'identifiant de l'utilisateur courant
	 * @param email  l'email à vérifier
	 * @throws EmailAlreadyExistsException si l'email est déjà utilisé par un autre
	 *                                     utilisateur
	 */
	@Override
	public void checkEmailUniqueness(Long userId, String email) {
		User existingUser = userRepository.findByEmail(email);
		if (existingUser != null && !existingUser.getId().equals(userId)) {
			throw new EmailAlreadyExistsException("Email déjà existant");
		}
	}
}