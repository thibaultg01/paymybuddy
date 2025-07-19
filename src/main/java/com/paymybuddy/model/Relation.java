package com.paymybuddy.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "relation")
@IdClass(Relation.RelationId.class)
public class Relation {

	@Id
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Id
	@ManyToOne
	@JoinColumn(name = "friend_id")
	private User friend;

	public Relation() {
	}

	public Relation(User user, User friend) {
		this.user = user;
		this.friend = friend;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getFriend() {
		return friend;
	}

	public void setFriend(User friend) {
		this.friend = friend;
	}

	// --- Classe clé composite ---
	public static class RelationId implements Serializable {
		private Long user;
		private Long friend;

		public RelationId() {
		}

		public RelationId(Long user, Long friend) {
			this.user = user;
			this.friend = friend;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (!(o instanceof RelationId))
				return false;
			RelationId that = (RelationId) o;
			return Objects.equals(user, that.user) && Objects.equals(friend, that.friend);
		}

		@Override
		public int hashCode() {
			return Objects.hash(user, friend);
		}
	}
}
