package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.api;

import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.Physician;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class PhysicianViewMapper {
    public abstract PhysicianView toPhysicianView(Physician physician);

    public abstract List<PhysicianView> toPhysicianView(List<Physician> physicians);
}
