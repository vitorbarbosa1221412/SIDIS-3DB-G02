
package com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.services;

import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.repositories.UserRepository;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository userRepo;

	// MÉTODOS DE ESCRITA (create, update, delete) FORAM REMOVIDOS.
	// Este serviço apenas lê dados que foram sincronizados via Eventos.

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		return userRepo.findByUsername(username).orElseThrow(
				() -> new UsernameNotFoundException(String.format("User with username - %s, not found", username)));
	}

	public boolean usernameExists(final String username) {
		return userRepo.findByUsername(username).isPresent();
	}

	public User getUser(final Long id) {
		return userRepo.findById(id).orElse(null);
	}

	public Optional<User> findByUsername(final String username) {
		return userRepo.findByUsername(username);
	}
}
