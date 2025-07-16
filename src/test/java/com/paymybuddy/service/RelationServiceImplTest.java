package com.paymybuddy.service;

import com.paymybuddy.dto.FriendDTO;
import com.paymybuddy.exception.RelationRulesException;
import com.paymybuddy.model.Relation;
import com.paymybuddy.repository.RelationRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.impl.RelationServiceImpl;
import com.paymybuddy.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class RelationServiceImplTest {

	@Mock
	private RelationRepository relationRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private RelationServiceImpl relationService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getFriendsOfUser_shouldReturnListOfFriend() {
		// given
		String userEmail = "carol@example.com";
		User user = new User();
		user.setEmail(userEmail);
		User friend1 = new User();
		friend1.setEmail("alice@example.com");
		User friend2 = new User();
		friend2.setEmail("bob@example.com");
		Relation relation1 = new Relation(user, friend1);
		Relation relation2 = new Relation(user, friend2);

		// when
		when(userRepository.findByEmail(userEmail)).thenReturn(user);
		when(relationRepository.findByUser(user)).thenReturn(Arrays.asList(relation1, relation2));
		List<String> friends = relationService.getRelationsEmails(userEmail);

		// then
		assertThat(friends).hasSize(2);
		assertThat(friends).containsExactlyInAnyOrder("alice@example.com", "bob@example.com");
	}

	@Test
	void addFriend_shouldCreateAndReturnNewRelation() {
		// given
		User user = new User();
		user.setEmail("bob@example.com");
		User friend = new User();
		friend.setEmail("alice@example.com");

		Relation relation = new Relation(user, friend);

		when(relationRepository.existsByUserAndFriend(user, friend)).thenReturn(false);
		when(relationRepository.save(any(Relation.class))).thenReturn(relation);

		// when
		Relation result = relationService.addFriend(user, friend);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getUser().getEmail()).isEqualTo("bob@example.com");
		assertThat(result.getFriend().getEmail()).isEqualTo("alice@example.com");

		verify(relationRepository).save(any(Relation.class));
	}

	@Test
	void addFriend_shouldThrowExceptionIfUserEqualsFriend() {
		// given
		User user = new User();
		user.setEmail("bob@example.com");

		// when + then
		assertThatThrownBy(() -> relationService.addFriend(user, user)).isInstanceOf(RelationRulesException.class)
				.hasMessage("Impossible d’ajouter soi-même comme ami.");

		verify(relationRepository, never()).save(any());
	}

	@Test
	void addFriend_shouldThrowExceptionIfRelationExists() {
		// given
		User user = new User();
		user.setEmail("bob@example.com");
		User friend = new User();
		friend.setEmail("alice@example.com");

		when(relationRepository.existsByUserAndFriend(user, friend)).thenReturn(true);

		// when + then
		assertThatThrownBy(() -> relationService.addFriend(user, friend)).isInstanceOf(RelationRulesException.class)
				.hasMessage("Relation déjà existante.");

		verify(relationRepository, never()).save(any());
	}

}