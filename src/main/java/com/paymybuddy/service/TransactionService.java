package com.paymybuddy.service;

import java.math.BigDecimal;
import java.util.List;

import com.paymybuddy.dto.TransactionDTO;

public interface TransactionService {
    void makeTransfer(String senderEmail, String recipientEmail, BigDecimal amount, String description);
    List<TransactionDTO> getTransactionHistory(String userEmail);
}