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

	@GetMapping("/login")
	public String showLoginForm() {
		logger.debug(" /login intercepté par AuthController");
		return "login";
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		logger.debug(" /register intercepté par AuthController");
		model.addAttribute("user", new User());
		return "register";
	}

	@PostMapping("/register/save")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, RedirectAttributes redirectAttributes) {
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