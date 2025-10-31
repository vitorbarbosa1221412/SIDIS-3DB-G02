package com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.services;

import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.Role;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.User;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class EditUserMapper {

	// Mapper para criar User
	@Mapping(source = "authorities", target = "authorities", qualifiedByName = "stringToRole")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "username", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "enabled", ignore = true)
	public abstract User create(CreateUserRequest request);

	// Mapper para atualizar User
	@BeanMapping(
			nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
			nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
	)
	@Mappings({
			// Campos editáveis do DTO
			@Mapping(source = "fullName", target = "fullName"),
			@Mapping(source = "authorities", target = "authorities", qualifiedByName = "stringToRole"),

			// Campos do target que não existem no DTO → ignorar
			@Mapping(target = "username", ignore = true),
			@Mapping(target = "password", ignore = true),
			@Mapping(target = "enabled", ignore = true),
			@Mapping(target = "id", ignore = true)
	})
	public abstract void update(EditUserRequest request, @MappingTarget User user);

	// Converte Set<String> em Set<Role>
	@Named("stringToRole")
	protected Set<Role> stringToRole(final Set<String> authorities) {
		if (authorities != null) {
			return authorities.stream().map(Role::new).collect(Collectors.toSet());
		}
		return new HashSet<>();
	}
}

