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

/**
 * Implémentation du service de gestion des transactions pour l'application Pay My Buddy.
 *
 * Cette classe gère :
 * - la logique métier pour effectuer un transfert d'argent entre deux utilisateurs
 * - la récupération de l'historique des transactions d'un utilisateur
 */
@Service
public class TransactionServiceImpl implements TransactionService {

	private static final Logger logger = LogManager.getLogger(TransactionServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	/**
	 * Effectue un transfert d'argent entre deux utilisateurs identifiés par leur adresse e-mail.
	 *
	 * Cette méthode :
	 * - vérifie l'existence du destinataire et de l'expéditeur
	 * - valide que l'expéditeur dispose de fonds suffisants
	 * - met à jour les soldes des deux utilisateurs
	 * - enregistre la transaction en base de données
	 *
	 * @param senderEmail     adresse e-mail de l'expéditeur
	 * @param recipientEmail  adresse e-mail du destinataire
	 * @param amount          montant du transfert
	 * @param description     description associée à la transaction
	 *
	 * @throws UserNotFoundException         si l’un des utilisateurs n’est pas trouvé
	 * @throws InsufficientBalanceException si le solde de l’expéditeur est insuffisant
	 */
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

	/**
	 * Récupère l’historique des transactions (envoyées et reçues) pour un utilisateur donné.
	 *
	 * Les transactions sont converties en objets TransactionDTO :
	 * - si l'utilisateur est l'expéditeur : le flag received est false
	 * - si l'utilisateur est le destinataire : le flag received est true
	 *
	 * @param userEmail adresse e-mail de l’utilisateur
	 * @return une liste de DTO représentant les transactions
	 */
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
			all.add(new TransactionDTO(t.getRecipient().getEmail(), t.getAmount(), t.getDescription(), false));
		}
		for (Transaction t : received) {
			all.add(new TransactionDTO(t.getSender().getEmail(), t.getAmount(), t.getDescription(), true));
		}

		return all;
	}
}
