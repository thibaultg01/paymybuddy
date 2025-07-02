package com.paymybuddy.exception;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.paymybuddy.model.User;

@ControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public String handleEmailExists(EmailAlreadyExistsException ex,
                                    @ModelAttribute("user") User user,
                                    BindingResult result,
                                    Model model) {
        result.rejectValue("email", "error.user", ex.getMessage());
        return "register";
    }
}
