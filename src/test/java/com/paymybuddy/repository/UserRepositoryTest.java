package com.paymybuddy.repository;

import com.paymybuddy.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {
/*
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserByEmail() {
        User user = new User();
        user.setEmail("test1@example.com");
        user.setPassword("securepassword");
        user.setUsername("existinguser");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setBalance(BigDecimal.valueOf(50.00));

        userRepository.save(user);

        Optional<User> retrieved = Optional.ofNullable(userRepository.findByEmail("test1@example.com"));
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getFirstName()).isEqualTo("Test");
    }*/
}