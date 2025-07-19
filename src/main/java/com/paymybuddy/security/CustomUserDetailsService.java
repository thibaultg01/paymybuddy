package com.paymybuddy.security;

import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implémentation personnalisée de {@link UserDetailsService} utilisée par Spring Security
 * pour charger les informations de l'utilisateur à partir de la base de données.
 *
 * Cette classe permet à Spring Security d’authentifier les utilisateurs en recherchant
 * leur email et en retournant un objet {@link UserDetails}.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Charge un utilisateur à partir de son adresse email.
     *
     * Cette méthode est appelée automatiquement par Spring Security lors de l’authentification.
     * Si l’utilisateur est trouvé en base de données, il est encapsulé dans un objet
     * {@link CustomUserDetails} qui contient les informations de sécurité.
     *
     * @param email l'adresse email de l’utilisateur
     * @return les détails de l’utilisateur sous forme de {@link UserDetails}
     * @throws UsernameNotFoundException si aucun utilisateur n’est trouvé avec cet email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Utilisateur introuvable avec l’email : " + email);
        }

		return new CustomUserDetails(user);
	}
}
