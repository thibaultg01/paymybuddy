package com.paymybuddy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;

/**
 * Interface du repository Spring Data JPA pour l'entité Transaction.
 *
 * Fournit des méthodes pour interroger les transactions en fonction de
 * l'expéditeur ou du destinataire.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	/**
	 * Recherche toutes les transactions envoyées par un utilisateur donné.
	 *
	 * @param sender l'utilisateur expéditeur
	 * @return une liste des transactions où l'utilisateur est l'expéditeur
	 */
	List<Transaction> findBySender(User sender);

	/**
	 * Recherche toutes les transactions reçues par un utilisateur donné.
	 *
	 * @param recipient l'utilisateur destinataire
	 * @return une liste des transactions où l'utilisateur est le destinataire
	 */
	List<Transaction> findByRecipient(User recipient);

	/**
	 * Recherche toutes les transactions où l'utilisateur est soit expéditeur, soit
	 * destinataire.
	 *
	 * @param user  l'utilisateur à rechercher comme expéditeur
	 * @param user2 l'utilisateur à rechercher comme destinataire
	 * @return toutes les transactions liées à cet utilisateur
	 */
	Iterable<? extends Transaction> findAllBySenderOrRecipient(User user, User user2);
}