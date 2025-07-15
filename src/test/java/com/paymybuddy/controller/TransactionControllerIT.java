package com.paymybuddy.controller;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private User sender;
    private User recipient;

    private void deleteUserAndTransactionsIfExists(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            transactionRepository.deleteAll(
                transactionRepository.findAllBySenderOrRecipient(user, user)
            );
            userRepository.delete(user);
        }
    }
    
    @BeforeEach
    void setup() {
       // transactionRepository.deleteAll();
       // userRepository.deleteAll();

    	 deleteUserAndTransactionsIfExists("sender@example.com");
    	    deleteUserAndTransactionsIfExists("recipient@example.com");
    	
        sender = new User();
        sender.setEmail("sender@example.com");
        sender.setPassword("password123");
        sender.setUsername("sender.user");
        sender.setFirstName("Alice");
        sender.setLastName("Sender");
        sender.setBalance(new BigDecimal("100.00"));
        userRepository.save(sender);

        recipient = new User();
        recipient.setEmail("recipient@example.com");
        recipient.setPassword("password456");
        recipient.setUsername("recipient.user");
        recipient.setFirstName("Bob");
        recipient.setLastName("Recipient");
        recipient.setBalance(new BigDecimal("50.00"));
        userRepository.save(recipient);
    }

    @Test
    void testTransfer_Success_AsAuthenticatedUser() throws Exception {
    	mockMvc.perform(post("/transfer")
                .param("relationEmail", "recipient@example.com")
                .param("amount", "25.00")
                .param("description", "Test transfert")
                .with(csrf())
                .with(user("sender@example.com").roles("USER")))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/transfer"))
            .andExpect(flash().attribute("success", "Transfert effectué avec succès."));
    }

    @Test
    void testTransfer_RecipientNotFound() throws Exception {
    	User user = userRepository.findByEmail("recipient@example.com");
            userRepository.delete(user);

        mockMvc.perform(post("/transfer")
                .param("relationEmail", "recipient@example.com")
                .param("amount", "25.00")
                .param("description", "Test erreur utilisateur")
                .with(csrf())
                .with(user("sender@example.com").roles("USER")))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/transfer"))
            .andExpect(flash().attribute("error", containsString("Utilisateur introuvable")));
    }

    @Test
    void testTransfer_InsufficientBalance() throws Exception {
    	sender.setBalance(new BigDecimal("10.00"));
        userRepository.save(sender);

        mockMvc.perform(post("/transfer")
                .param("relationEmail", "recipient@example.com")
                .param("amount", "25.00")
                .param("description", "Test solde insuffisant")
                .with(csrf())
                .with(user("sender@example.com").roles("USER")))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/transfer"))
            .andExpect(flash().attribute("error", containsString("Fonds insuffisants pour l'utilisateur : sender@example.com")));
    }
}
