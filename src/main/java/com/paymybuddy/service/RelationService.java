package com.paymybuddy.service;

import com.paymybuddy.model.User;
import com.paymybuddy.model.Relation;

import java.util.List;

public interface RelationService {
    Relation addFriend(User user, User friend);
    List<String> getRelationsEmails(String userEmail);
    User getUserByEmail(String email);
}