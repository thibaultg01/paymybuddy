package com.paymybuddy.controller;

import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/web/users")
public class UserWebController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showUserList(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users"; // rend users.html
    }

    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        return "add-user"; // rend add-user.html
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user) {
        userService.createUser(user);
        return "redirect:/web/users";
    }
}