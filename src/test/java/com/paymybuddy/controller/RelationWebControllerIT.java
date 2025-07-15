package com.paymybuddy.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.paymybuddy.model.Relation;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.RelationRepository;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RelationWebControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RelationRepository relationRepository;

    private User sender;
    private User friend;

    @BeforeEach
    void setup() {
        deleteUserIfExists("sender@example.com");
        deleteUserIfExists("friend@example.com");

        sender = new User();
        sender.setEmail("sender@example.com");
        sender.setPassword("password123");
        sender.setUsername("sender.user");
        sender.setFirstName("Alice");
        sender.setLastName("Sender");
        sender.setBalance(new BigDecimal("100.00"));
        userRepository.save(sender);

        friend = new User();
        friend.setEmail("friend@example.com");
        friend.setPassword("password456");
        friend.setUsername("friend.user");
        friend.setFirstName("Bob");
        friend.setLastName("Friend");
        friend.setBalance(new BigDecimal("50.00"));
        userRepository.save(friend);
    }
    
    private void deleteUserAndTransactionsIfExists(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            transactionRepository.deleteAll(
                transactionRepository.findAllBySenderOrRecipient(user, user)
            );
            userRepository.delete(user);
        }
    }

    private void deleteUserIfExists(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
        	deleteUserAndTransactionsIfExists(email);
            relationRepository.deleteAllByUser(user);
            userRepository.delete(user);
        }
    }

    @Test
    void testAddRelation_Success() throws Exception {
        mockMvc.perform(post("/relation/add")
                .param("email", "friend@example.com")
                .with(csrf())
                .with(user("sender@example.com").roles("USER")))
            .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/relation/add?success"));
    }

    @Test
    void testAddRelation_UserNotFound() throws Exception {
        mockMvc.perform(post("/relation/add")
                        .param("email", "unknown@example.com")
                        .with(csrf())
                        .with(user("sender@example.com").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/relation/add"))
                .andExpect(flash().attribute("error", "Utilisateur introuvable"));
    }
}

