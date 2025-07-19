package com.paymybuddy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.model.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(EmailAlreadyExistsException.class)
	public String handleEmailExists(EmailAlreadyExistsException ex, @ModelAttribute("user") User user,
			BindingResult result, Model model) {
		result.rejectValue("email", "error.user", ex.getMessage());
		return "register";
	}

	@ExceptionHandler(UserNotFoundException.class)
	public String handleUserNotFound(UserNotFoundException ex, RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("error", ex.getMessage());
		return "redirect:" + ex.getRedirectUrl();
	}

	@ExceptionHandler(InsufficientBalanceException.class)
	public String handleInsufficientBalance(InsufficientBalanceException ex, RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("error", ex.getMessage());
		return "redirect:/transfer";
	}

	@ExceptionHandler(Exception.class)
	public String handleGenericException(Exception ex, RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("error", "Une erreur est survenue : " + ex.getMessage());
		return "redirect:/profile";
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public String handleResourceNotFound(Exception ex, RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("error", "Une erreur est survenue : " + ex.getMessage());
		return "redirect:/relation/add";
	}

	@ExceptionHandler(RelationRulesException.class)
	public String handleRules(Exception ex, RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("error", ex.getMessage());
		return "redirect:/relation/add";
	}
}