package com.paymybuddy.security;

import com.paymybuddy.security.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de la sécurité Spring Security pour l'application Pay My Buddy.
 *
 * Cette classe configure : - le gestionnaire d'authentification basé sur un
 * service utilisateur personnalisé - l'encodage des mots de passe avec BCrypt -
 * la stratégie de filtrage des requêtes HTTP pour sécuriser les accès
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	 /**
     * Fournit un encodeur de mot de passe utilisant l’algorithme BCrypt.
     *
     * @return un encodeur sécurisé pour les mots de passe
     */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	 /**
     * Configure le gestionnaire d'authentification à utiliser avec le service
     * utilisateur personnalisé et l’encodeur de mot de passe défini.
     *
     * @param http l'objet HttpSecurity partagé par Spring Security
     * @return le gestionnaire d’authentification configuré
     * @throws Exception en cas d'erreur de configuration
     */
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
		builder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
		return builder.build();
	}

	/**
     * Définit la chaîne de filtres de sécurité et les règles d’autorisation.
     *
     * Autorise l'accès libre aux pages de connexion, d’inscription et aux ressources statiques,
     * tout en exigeant une authentification pour les autres requêtes.
     *
     * @param http l'objet de configuration HttpSecurity
     * @return la chaîne de filtres de sécurité
     * @throws Exception en cas d'erreur de configuration
     */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth.requestMatchers("/login", "/register", "/register/save", "/css/**")
				.permitAll().anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/transfer", true).permitAll())
				.logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll());

		return http.build();
	}
}