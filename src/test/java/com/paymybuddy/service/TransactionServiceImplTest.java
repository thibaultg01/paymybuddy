package com.paymybuddy.service;

import com.paymybuddy.dto.TransactionDTO;
import com.paymybuddy.exception.InsufficientBalanceException;
import com.paymybuddy.exception.UserNotFoundException;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

	@InjectMocks
	private TransactionServiceImpl transactionService;

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private UserRepository userRepository;

	private User sender;
	private User recipient;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		sender = new User();
		sender.setEmail("sender@example.com");
		sender.setBalance(new BigDecimal("100.00"));

		recipient = new User();
		recipient.setEmail("recipient@example.com");
		recipient.setBalance(new BigDecimal("50.00"));
	}

	@Test
	void testMakeTransfer_Success() {
		// GIVEN
		BigDecimal amount = new BigDecimal("20.00");
		String description = "Test payment";

		when(userRepository.findByEmail("sender@example.com")).thenReturn(sender);
		when(userRepository.findByEmail("recipient@example.com")).thenReturn(recipient);

		// WHEN
		transactionService.makeTransfer("sender@example.com", "recipient@example.com", amount, description);

		// THEN
		assertEquals(new BigDecimal("80.00"), sender.getBalance());
		assertEquals(new BigDecimal("70.00"), recipient.getBalance());

		verify(transactionRepository, times(1)).save(any(Transaction.class));
		verify(userRepository, times(1)).save(sender);
		verify(userRepository, times(1)).save(recipient);
	}

	@Test
	void testMakeTransfer_SenderNotFound() {
		when(userRepository.findByEmail("sender@example.com")).thenReturn(null);

		Exception exception = assertThrows(UserNotFoundException.class, () -> transactionService
				.makeTransfer("sender@example.com", "recipient@example.com", new BigDecimal("20.00"), "Test"));

		assertTrue(exception.getMessage().contains("Utilisateur introuvable avec l'email"));
	}

	@Test
	void testMakeTransfer_RecipientNotFound() {
		when(userRepository.findByEmail("sender@example.com")).thenReturn(sender);
		when(userRepository.findByEmail("recipient@example.com")).thenReturn(null);

		Exception exception = assertThrows(UserNotFoundException.class, () -> transactionService
				.makeTransfer("sender@example.com", "recipient@example.com", new BigDecimal("20.00"), "Test"));

		assertTrue(exception.getMessage().contains("Utilisateur introuvable avec l'email"));
	}

	@Test
	void testMakeTransfer_InsufficientBalance() {
		sender.setBalance(new BigDecimal("5.00"));
		when(userRepository.findByEmail("sender@example.com")).thenReturn(sender);
		when(userRepository.findByEmail("recipient@example.com")).thenReturn(recipient);

		Exception exception = assertThrows(InsufficientBalanceException.class, () -> transactionService
				.makeTransfer("sender@example.com", "recipient@example.com", new BigDecimal("20.00"), "Test"));

		assertTrue(exception.getMessage().contains("Fonds insuffisants"));
	}

}
