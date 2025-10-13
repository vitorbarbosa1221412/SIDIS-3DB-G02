package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.api;



import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.AppointmentNumber;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.services.CreateAppointmentRequest;
import org.springframework.stereotype.Component;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.Appointment;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(source = "appointmentNumber.value", target = "appointmentNumber")
    @Mapping(source = "patient.id",   target = "patientId")
    @Mapping(source = "physician.id", target = "physicianId")
    @Mapping(source = "dateTime",     target = "appointmentDate")
    AppointmentView toView(Appointment appointment);

    List<AppointmentView> toView(List<Appointment> appointments);

    @Mapping(target = "appointmentNumber", expression = "java(new AppointmentNumber(view.getAppointmentNumber()))")
    @Mapping(source = "patientId",   target = "patient.id")
    @Mapping(source = "physicianId", target = "physician.id")
    @Mapping(source = "appointmentDate", target = "dateTime")
    Appointment toEntity(AppointmentView view);

    @Mapping(source = "appointmentDate", target = "dateTime")
    void updateFromView(AppointmentView view, @MappingTarget Appointment appointment);

    default AppointmentNumber map(String value) {
        return new AppointmentNumber(value);
    }

    default String map(AppointmentNumber number) {
        return number.getValue();
    }
}
