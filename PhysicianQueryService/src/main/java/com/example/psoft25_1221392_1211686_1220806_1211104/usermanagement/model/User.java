package com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Document(collection = "users")
@Getter
@Setter
public class User implements UserDetails {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long version;

	private LocalDateTime createdAt;

	private LocalDateTime modifiedAt;

	private String createdBy;

	private String modifiedBy;

	private boolean enabled = true;


	@Email
	@NotNull
	@NotBlank
	private String username;

	@NotNull
	@NotBlank
	private String password;

	private String fullName;

	//O Mongo guarda listas nativamente.
	private Set<Role> authorities = new HashSet<>();

	public User() {

	}

	public User(final String username, final String password) {
		this.username = username;
		setPassword(password);
	}

	public static User newUser(final String username, final String password, final String fullName) {
		final var u = new User(username, password);
		u.setFullName(fullName);
		return u;
	}

	public static User newUser(final String username, final String password, final String fullName, final String role) {
		final var u = new User(username, password);
		u.setFullName(fullName);
		u.addAuthority(new Role(role));
		return u;
	}

	public void setPassword(final String password) {
		this.password = Objects.requireNonNull(password);
	}

	public void addAuthority(Role role) {
		this.authorities.add(role);
	}

	@Override
	public boolean isAccountNonExpired() {
		return isEnabled();
	}

	@Override
	public boolean isAccountNonLocked() {
		return isEnabled();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return isEnabled();
	}
}
