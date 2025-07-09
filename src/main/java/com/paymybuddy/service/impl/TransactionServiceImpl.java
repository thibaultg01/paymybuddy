package com.paymybuddy.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.controller.TransactionController;
import com.paymybuddy.dto.TransactionDTO;
import com.paymybuddy.exception.InsufficientBalanceException;
import com.paymybuddy.exception.UserNotFoundException;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {

	private static final Logger logger = LogManager.getLogger(TransactionServiceImpl.class);
	
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public void makeTransfer(String senderEmail, String recipientEmail, BigDecimal amount, String description) {
    	User sender = userRepository.findByEmail(senderEmail);
        if (sender == null) {
        	throw new UserNotFoundException("Utilisateur introuvable avec l'email : " + senderEmail, "/transfer");
        }

        User recipient = userRepository.findByEmail(recipientEmail);
        if (recipient == null) {
        	throw new UserNotFoundException("Utilisateur introuvable avec l'email : " + recipientEmail, "/transfer");
        }

        BigDecimal transferAmount = amount.setScale(2, RoundingMode.HALF_EVEN);

        if (sender.getBalance().compareTo(transferAmount) < 0) {
            throw new InsufficientBalanceException("Fonds insuffisants pour l'utilisateur : " + senderEmail);
        }

        sender.setBalance(sender.getBalance().subtract(transferAmount).setScale(2, RoundingMode.HALF_EVEN));
        recipient.setBalance(recipient.getBalance().add(transferAmount).setScale(2, RoundingMode.HALF_EVEN));

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setRecipient(recipient);
        transaction.setAmount(transferAmount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setDescription(description);

        transactionRepository.save(transaction);
        userRepository.save(sender);
        userRepository.save(recipient);
    }

    @Override
    public List<TransactionDTO> getTransactionHistory(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        logger.info("List<Transaction> sent = transactionRepository.findBySender(user);");
        List<Transaction> sent = transactionRepository.findBySender(user);
        logger.info("List<Transaction> received = transactionRepository.findByRecipient(user);");
        List<Transaction> received = transactionRepository.findByRecipient(user);
        logger.info("List<TransactionDTO> all = new ArrayList<>();");
        List<TransactionDTO> all = new ArrayList<>();

        for (Transaction t : sent) {
            all.add(new TransactionDTO(t.getRecipient().getEmail(), t.getAmount(),t.getDescription(), false));
        }
        for (Transaction t : received) {
            all.add(new TransactionDTO(t.getSender().getEmail(), t.getAmount(),t.getDescription(), true));
        }

        return all;
    }
}

