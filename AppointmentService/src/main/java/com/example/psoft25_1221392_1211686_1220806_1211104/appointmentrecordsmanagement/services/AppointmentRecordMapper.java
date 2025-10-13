package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.services;

import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.model.AppointmentRecord;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AppointmentRecordMapper {

    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "recordNumber", ignore = true)
    AppointmentRecord create(CreateAppointmentRecordRequest request);

    @BeanMapping(
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    void update(UpdateAppointmentRecordRequest request, @MappingTarget AppointmentRecord appointmentRecord);
}






