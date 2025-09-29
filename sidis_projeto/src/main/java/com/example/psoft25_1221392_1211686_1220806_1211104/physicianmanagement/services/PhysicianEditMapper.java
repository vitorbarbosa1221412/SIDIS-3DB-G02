package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.services;

import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.Physician;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class PhysicianEditMapper {
    @Mapping(source = "emailAddress", target = "emailAddress")
    public abstract Physician create(CreatePhysicianRequest request);

    @BeanMapping(
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    public abstract void update(UpdatePhysicianRequest request, @MappingTarget Physician physician);
}
