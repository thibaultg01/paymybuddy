package com.paymybuddy.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.paymybuddy.dto.FriendDTO;
import com.paymybuddy.exception.ResourceNotFoundException;
import com.paymybuddy.model.User;
import com.paymybuddy.service.RelationService;
import com.paymybuddy.service.UserService;

@Controller
@RequestMapping("/relation")
public class RelationWebController {

	private static final Logger logger = LogManager.getLogger(RelationWebController.class);
	
    @Autowired
    private RelationService relationService;

    @Autowired
    private UserService userService;

    @GetMapping("/add")
    public String showAddFriendForm(Model model) {
        model.addAttribute("friendDTO", new FriendDTO());
        return "add-friend";
    }

    @PostMapping("/add")
    public String addFriend(@ModelAttribute("friendDTO") FriendDTO friendDTO) {
        // Récupérer l'email de l'utilisateur connecté
    	logger.debug("tentative obtention email");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug("tentative obtention utilisateur");
        User user = relationService.getUserByEmail(email);
        logger.debug("tentative obtention utilisateur amis");
        User friend = relationService.getUserByEmail(friendDTO.getEmail());
        if (friend == null) {
            throw new ResourceNotFoundException("Aucun utilisateur trouvé avec cet email.");
        }

        relationService.addFriend(user, friend);
        return "redirect:/relation/add?success";
    }
}