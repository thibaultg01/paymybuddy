package com.paymybuddy.repository;

import com.paymybuddy.model.Relation;
import com.paymybuddy.model.Relation.RelationId;
import com.paymybuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationRepository extends JpaRepository<Relation, RelationId> {

    List<Relation> findByUser(User user);
    //List<Relation> findByEmail(Mail email);
    boolean existsByUserAndFriend(User user, User friend);
}