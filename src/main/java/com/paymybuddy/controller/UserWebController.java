package com.paymybuddy.controller;

import com.paymybuddy.exception.EmailAlreadyExistsException;
import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.Principal;

@Controller
public class UserWebController {

	private static final Logger logger = LogManager.getLogger(UserWebController.class);

	@Autowired
	private UserService userService;

	@GetMapping("/profile")
	public String showProfile(Model model, Principal principal) {
		logger.debug("Chargement du profil pour l'utilisateur : {}", principal.getName());
		User user = userService.getUserByEmail(principal.getName());
		model.addAttribute("user", user);
		return "profile"; // -> templates/profile.html
	}

	@GetMapping("/profile/edit")
	public String showEditProfileForm(Model model, Principal principal) {
		logger.debug("Affichage du formulaire de modification pour : {}", principal.getName());
		User user = userService.getUserByEmail(principal.getName());
		model.addAttribute("user", user);
		return "edit-profile";
	}

	@PostMapping("/profile/edit")
	public String updateProfile(@ModelAttribute("user") @Valid User updatedUser, BindingResult result,
			Principal principal, RedirectAttributes redirectAttributes) {
		logger.debug("tentative de mise à jour du profil pour : {}", principal.getName());
		User currentUser = userService.getUserByEmail(principal.getName());

		if (!currentUser.getEmail().equals(updatedUser.getEmail())) {
			logger.debug("Changement d'adresse email détecté : {} -> {}", currentUser.getEmail(),
					updatedUser.getEmail());
			try {
				userService.checkEmailUniqueness(currentUser.getId(), updatedUser.getEmail());
			} catch (EmailAlreadyExistsException ex) {
				logger.error("Email déjà utilisé : {}", updatedUser.getEmail(), ex);
				result.rejectValue("email", "error.user", ex.getMessage());
			}
		}

		if (result.hasErrors()) {
			return "edit-profile";
		}

		userService.updateUser(currentUser.getId(), updatedUser);

		redirectAttributes.addFlashAttribute("success", "Profil mis à jour avec succès.");
		return "redirect:/profile";
	}
}
