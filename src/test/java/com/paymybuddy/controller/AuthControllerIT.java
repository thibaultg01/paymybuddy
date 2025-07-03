package com.paymybuddy.controller;

import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void loginPage_ShouldReturnLoginView() throws Exception {
        mockMvc.perform(post("/login"))
               .andExpect(status().isOk())
               .andExpect(view().name("login"));
    }

    @Test
    void registerPage_ShouldReturnRegisterView() throws Exception {
        mockMvc.perform(post("/register"))
               .andExpect(status().isOk())
               .andExpect(view().name("register"));
    }

    @Test
    void registerSave_ShouldCreateUserAndRedirect() throws Exception {
        User existing = userRepository.findByEmail("newuser@example.com");
        if (existing != null) {
            userRepository.delete(existing);
        }

        mockMvc.perform(post("/register/save")
                .param("username", "newuser")
                .param("email", "newuser@example.com")
                .param("password", "mypassword")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login?registerSuccess"));

        User savedUser = userRepository.findByEmail("newuser@example.com");
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("newuser");
        assertThat(passwordEncoder.matches("mypassword", savedUser.getPassword())).isTrue();
    }
    
    @Test
    void registerSave_ShouldRedirectBack_WhenEmailAlreadyExists() throws Exception {
        User existingUser = new User();
        existingUser.setUsername("existinguser");
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword(passwordEncoder.encode("secret"));
        userRepository.save(existingUser);

        mockMvc.perform(post("/register/save")
                .param("username", "newuser")
                .param("email", "existing@example.com") // email déjà pris
                .param("password", "newpassword")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/register"))
            .andExpect(flash().attributeExists("emailError"))
            .andExpect(flash().attribute("emailError", "Email déjà existant"));
    }
}
