package com.paymybuddy.controller;

import com.paymybuddy.dto.TransactionDTO;
import com.paymybuddy.exception.GlobalExceptionHandler;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.RelationService;
import com.paymybuddy.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    

    @MockBean
    private RelationService relationService;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void testShowTransferPage_AddsAttributesAndReturnsView() throws Exception {
        String userEmail = "sender@example.com";

        List<String> relations = List.of("bob@example.com", "carol@example.com");
        List<TransactionDTO> transactions = List.of(
            new TransactionDTO("bob@example.com", new BigDecimal("20.00"), "Dîner", false),
            new TransactionDTO("carol@example.com", new BigDecimal("15.00"), "Transport", true)
        );

        when(relationService.getRelationsEmails(userEmail)).thenReturn(relations);
        when(transactionService.getTransactionHistory(userEmail)).thenReturn(transactions);

        mockMvc.perform(get("/transfer")
                .with(user(userEmail).roles("USER")))
            .andExpect(status().isOk())
            .andExpect(view().name("transfer"))
            .andExpect(model().attribute("relations", relations))
            .andExpect(model().attribute("transactions", transactions));
    }
}
