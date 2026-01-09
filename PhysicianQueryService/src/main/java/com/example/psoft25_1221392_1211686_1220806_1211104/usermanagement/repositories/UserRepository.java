package com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.repositories;

import com.example.psoft25_1221392_1211686_1220806_1211104.exceptions.NotFoundException;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {

	default User getById(final Long id) {
		final Optional<User> maybeUser = findById(id);
		return maybeUser.filter(User::isEnabled)
				.orElseThrow(() -> new NotFoundException(User.class, id));
	}

	// Query Method do Mongo
	Optional<User> findByUsername(String username);
}