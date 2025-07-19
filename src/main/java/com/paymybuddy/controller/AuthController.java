package com.paymybuddy.controller;

import com.paymybuddy.exception.EmailAlreadyExistsException;
import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

	private static final Logger logger = LogManager.getLogger(AuthController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Affiche le formulaire de connexion.
	 *
	 * @return le nom du template de la page de connexion ("login")
	 */
	@GetMapping("/login")
	public String showLoginForm() {
		logger.debug(" /login intercepté par AuthController");
		return "login";
	}

	/**
	 * Affiche le formulaire d'inscription.
	 *
	 * Initialise un objet utilisateur vide dans le modèle pour le binding du formulaire.
	 *
	 * @param model le modèle contenant l’attribut "user"
	 * @return le nom du template de la page d'inscription ("register")
	 */
	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		logger.debug(" /register intercepté par AuthController");
		model.addAttribute("user", new User());
		return "register";
	}

	/**
	 * Traite la soumission du formulaire d'inscription.
	 *
	 * Vérifie les erreurs de validation et l’unicité de l’adresse email.
	 * En cas d’erreur, les messages sont renvoyés à la vue via les attributs flash.
	 * En cas de succès, l’utilisateur est redirigé vers la page de connexion.
	 *
	 * @param user                les données saisies dans le formulaire
	 * @param result              le résultat de la validation
	 * @param redirectAttributes  les attributs flash pour transmettre les erreurs ou succès
	 * @return la redirection vers la page appropriée ("/register" ou "/login?registerSuccess")
	 */
	@PostMapping("/register/save")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result,
			RedirectAttributes redirectAttributes) {
		logger.debug(" /register/save intercepté par AuthController");
		if (result.hasErrors()) {
			logger.debug("Des erreurs de validation ont été détectées dans le formulaire d'inscription.");
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", result);
			redirectAttributes.addFlashAttribute("user", user);
			return "redirect:/register";
		}
		try {
			userService.createUser(user);
			logger.info("Nouvel utilisateur enregistré avec succès : {}", user.getEmail());
		} catch (EmailAlreadyExistsException e) {
			redirectAttributes.addFlashAttribute("emailError", e.getMessage());
			redirectAttributes.addFlashAttribute("user", user);
			return "redirect:/register";
		}
		return "redirect:/login?registerSuccess";
	}
}