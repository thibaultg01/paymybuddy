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

/**
 * Contrôleur Spring MVC responsable de la gestion des transferts d'argent entre
 * utilisateurs dans l'application Pay My Buddy.
 *
 * Ce contrôleur permet : - d'afficher la page de transfert avec l'historique
 * des transactions et les relations existantes - d'effectuer un transfert
 * d'argent vers un contact
 *
 * Les requêtes HTTP sont mappées à l’URL "/transfer".
 */
@Controller
@RequestMapping("/transfer")
public class TransactionController {

	private static final Logger logger = LogManager.getLogger(TransactionController.class);

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private RelationService relationService;

	/**
	 * Affiche la page de transfert pour l'utilisateur connecté.
	 *
	 * Cette méthode : - récupère l’email de l’utilisateur connecté - obtient la
	 * liste de ses relations - obtient l’historique de ses transactions - ajoute
	 * ces informations au modèle
	 *
	 * @param model     le modèle utilisé pour transmettre les attributs à la vue
	 * @param principal l'objet représentant l'utilisateur connecté
	 * @return le nom du template Thymeleaf à afficher ("transfer")
	 */
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
		logger.info("Obtention de la liste des transaction reussi");
		return "transfer"; // => templates/transfer.html
	}

	/**
	 * Traite un transfert d'argent soumis via le formulaire.
	 *
	 * Cette méthode appelle le service de transaction pour effectuer le transfert
	 * entre l'utilisateur connecté et l’email de la relation cible, avec le montant
	 * et la description fournis. Elle gère les erreurs comme le solde insuffisant
	 * ou un utilisateur inconnu.
	 *
	 * @param relationEmail      l’email du destinataire du transfert
	 * @param amount             le montant à transférer
	 * @param description        la description associée au transfert
	 * @param principal          l'utilisateur actuellement connecté
	 * @param redirectAttributes attributs flash utilisés pour afficher les messages
	 *                           dans la vue
	 * @return une redirection vers la page de transfert
	 */
	@PostMapping
	public String processTransfer(@RequestParam String relationEmail, @RequestParam BigDecimal amount,
			@RequestParam String description, Principal principal, RedirectAttributes redirectAttributes) {
		logger.debug("requete /transfert reçu");
		try {
			logger.debug("tentative transfert");
			transactionService.makeTransfer(principal.getName(), relationEmail, amount, description);
			redirectAttributes.addFlashAttribute("success", "Transfert effectué avec succès.");
		} catch (UserNotFoundException | InsufficientBalanceException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Une erreur est survenue.");
		}
		logger.info("Transfert effectué avec succès.");
		return "redirect:/transfer";
	}
}
