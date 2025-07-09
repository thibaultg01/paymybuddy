package com.paymybuddy.service.impl;

import com.paymybuddy.model.User;
import com.paymybuddy.exception.RelationRulesException;
import com.paymybuddy.exception.ResourceNotFoundException;
import com.paymybuddy.exception.UserNotFoundException;
import com.paymybuddy.model.Relation;
import com.paymybuddy.repository.RelationRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RelationServiceImpl implements RelationService {

	@Autowired
    private RelationRepository relationRepository;
	
	@Autowired
    private  UserRepository userRepository;

    @Override
    public Relation addFriend(User user, User friend) {
        if (user.equals(friend)) {
            throw new RelationRulesException("Impossible d’ajouter soi-même comme ami.");
        }

        if (relationRepository.existsByUserAndFriend(user, friend)) {
            throw new RelationRulesException("Relation déjà existante.");
        }

        Relation relation = new Relation(user, friend);
        return relationRepository.save(relation);
    }

    @Override
    public List<Relation> getRelations(User user) {
        return relationRepository.findByUser(user);
    }
    
    @Override
	public User getUserByEmail(String email) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UserNotFoundException("Utilisateur introuvable", "/relation/add");
		}
		return user;
	}
    
    @Override
    public List<String> getRelationsEmails(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
       /* if (user == null) {
            throw new UserNotFoundException(userEmail);
        }*/

        return relationRepository.findByUser(user).stream()
                .map(rel -> rel.getUser().getEmail())
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteFriend(Long userId, Long friendId) {
        Relation.RelationId relationId = new Relation.RelationId(userId, friendId);

        if (!relationRepository.existsById(relationId)) {
            throw new ResourceNotFoundException("Relation introuvable entre user " + userId + " et " + friendId);
        }

        relationRepository.deleteById(relationId);
    }
}