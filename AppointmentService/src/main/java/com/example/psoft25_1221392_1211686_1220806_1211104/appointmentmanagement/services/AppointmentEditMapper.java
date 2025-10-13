package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.services;

import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.Appointment;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AppointmentEditMapper {
    Appointment create(CreateAppointmentRequest request);

    @BeanMapping(
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    void update(UpdateAppointmentRequest request, @MappingTarget Appointment appointment);
}
