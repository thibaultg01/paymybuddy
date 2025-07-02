package com.paymybuddy.controller;

import com.paymybuddy.exception.EmailAlreadyExistsException;
import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
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

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/login")
	public String showLoginForm() {
		System.out.println(">>>> /login intercepté par AuthController");
		return "login";
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());
		return "register";
	}

	@PostMapping("/register/save")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, RedirectAttributes redirectAttributes) {
		 if (result.hasErrors()) {
	            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", result);
	            redirectAttributes.addFlashAttribute("user", user);
	            return "redirect:/register";
	        }
		try {
	        userService.createUser(user);
	    } catch (EmailAlreadyExistsException e) {
	    	redirectAttributes.addFlashAttribute("emailError", e.getMessage());
	        redirectAttributes.addFlashAttribute("user", user);
	        return "redirect:/register";
	    }

	    return "redirect:/login?registerSuccess";
	}
}