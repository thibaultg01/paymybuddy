package com.paymybuddy.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.dto.TransactionDTO;
import com.paymybuddy.exception.InsufficientBalanceException;
import com.paymybuddy.exception.UserNotFoundException;
import com.paymybuddy.service.RelationService;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.UserService;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/transfer")
public class TransactionController {

	private static final Logger logger = LogManager.getLogger(TransactionController.class);
	
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private RelationService relationService;
    @GetMapping
    public String showTransferPage(Model model, Principal principal) {
    	logger.debug("tentative recuperation email");
        String userEmail = principal.getName();
        logger.debug("tentative recuperation relations");
        List<String> relations = relationService.getRelationsEmails(userEmail);
        logger.debug("tentative ajout attribut relations");
        model.addAttribute("relations", relations);
        logger.debug("tentative recuperation transaction");
        List<TransactionDTO> transactions = transactionService.getTransactionHistory(userEmail);
        logger.debug("tentative ajout attribut transaction");
        model.addAttribute("transactions", transactions);

        return "transfer"; // => templates/transfer.html
    }

    @PostMapping
    public String processTransfer(@RequestParam String relationEmail,
                                  @RequestParam BigDecimal amount,
                                  @RequestParam String description,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes) {
        try {
            transactionService.makeTransfer(principal.getName(), relationEmail, amount, description);
            redirectAttributes.addFlashAttribute("success", "Transfert effectué avec succès.");
        } catch (UserNotFoundException | InsufficientBalanceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur est survenue.");
        }

        return "redirect:/transfer";
    }
}
