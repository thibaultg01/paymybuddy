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

        Exception exception = assertThrows(UserNotFoundException.class, () ->
            transactionService.makeTransfer("sender@example.com", "recipient@example.com", new BigDecimal("20.00"), "Test")
        );

        assertTrue(exception.getMessage().contains("Utilisateur introuvable avec l'email"));
    }

    @Test
    void testMakeTransfer_RecipientNotFound() {
        when(userRepository.findByEmail("sender@example.com")).thenReturn(sender);
        when(userRepository.findByEmail("recipient@example.com")).thenReturn(null);

        Exception exception = assertThrows(UserNotFoundException.class, () ->
            transactionService.makeTransfer("sender@example.com", "recipient@example.com", new BigDecimal("20.00"), "Test")
        );

        assertTrue(exception.getMessage().contains("Utilisateur introuvable avec l'email"));
    }

    @Test
    void testMakeTransfer_InsufficientBalance() {
        sender.setBalance(new BigDecimal("5.00"));
        when(userRepository.findByEmail("sender@example.com")).thenReturn(sender);
        when(userRepository.findByEmail("recipient@example.com")).thenReturn(recipient);

        Exception exception = assertThrows(InsufficientBalanceException.class, () ->
            transactionService.makeTransfer("sender@example.com", "recipient@example.com", new BigDecimal("20.00"), "Test")
        );

        assertTrue(exception.getMessage().contains("Fonds insuffisants"));
    }
    
    /*@Test
    void testGetTransactionHistory_ReturnsCorrectDTOList() {
        String email = "user@example.com";
        User user = new User(); user.setEmail(email);

        User friend1 = new User(); friend1.setEmail("friend1@example.com");
        User friend2 = new User(); friend2.setEmail("friend2@example.com");

        Transaction sent = new Transaction();
        sent.setSender(user);
        sent.setRecipient(friend1);
        sent.setAmount(new BigDecimal("10.00"));
        sent.setDescription("Envoyé à friend1");

        Transaction received = new Transaction();
        received.setSender(friend2);
        received.setRecipient(user);
        received.setAmount(new BigDecimal("20.00"));
        received.setDescription("Reçu de friend2");

        when(userRepository.findByEmail(email)).thenReturn(user);
        when(transactionRepository.findBySender(user)).thenReturn(List.of(sent));
        when(transactionRepository.findByRecipient(user)).thenReturn(List.of(received));

        List<TransactionDTO> history = transactionService.getTransactionHistory(email);

        assertEquals(2, history.size());

        TransactionDTO sentDTO = new TransactionDTO(
        	    "friend1@example.com",
        	    new BigDecimal("10.00"), // <- vrai BigDecimal
        	    "test",
        	    false
        	);

        TransactionDTO receivedDTO = history.stream()
            .filter(dto -> "friend2@example.com".equals(dto.getEmail()))
            .findFirst()
            .orElse(null);

        assertNotNull(sentDTO);
        assertNotNull(receivedDTO);

        assertEquals("-10.00€", formatDTO(sentDTO));
        assertEquals("20.00€", formatDTO(receivedDTO));
    }

    private String formatDTO(TransactionDTO dto) {
        boolean isReceived = dto.getEmail().equals("friend2@example.com");
        String sign = isReceived ? "" : "-";

        BigDecimal amount = new BigDecimal(dto.getAmount().replace("€", "")).setScale(2, RoundingMode.HALF_EVEN);
        return sign + amount.toPlainString() + "€";
    }*/
}
