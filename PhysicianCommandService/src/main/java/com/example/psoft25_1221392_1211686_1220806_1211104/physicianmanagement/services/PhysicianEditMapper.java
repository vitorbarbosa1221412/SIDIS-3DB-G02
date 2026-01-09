package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.services;

import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.Physician;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class PhysicianEditMapper {

    // Mapper para criar Physician
    @Mapping(source = "emailAddress", target = "emailAddress")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "profilePicture", ignore = true)
    @Mapping(target = "physicianNumber", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    public abstract Physician create(CreatePhysicianRequest request);

    // Mapper para atualizar Physician
    @BeanMapping(
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mappings({
            // Campos editáveis
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "specialty", target = "specialty"),

            // Campos ignorados
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "user", ignore = true),
            @Mapping(target = "profilePicture", ignore = true),
            @Mapping(target = "physicianNumber", ignore = true),
            @Mapping(target = "enabled", ignore = true)
    })
    public abstract void update(UpdatePhysicianRequest request, @MappingTarget Physician physician);
}

