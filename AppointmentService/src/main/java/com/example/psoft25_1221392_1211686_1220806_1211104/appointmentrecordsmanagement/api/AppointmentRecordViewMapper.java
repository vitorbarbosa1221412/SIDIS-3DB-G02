package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.api;

import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.model.AppointmentRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AppointmentRecordViewMapper {

    @Mappings({
            @Mapping(source = "diagnosis", target = "diagnosis"),
            @Mapping(source = "treatmentRecommendation", target = "treatmentRecommendation"),
            @Mapping(source = "prescription", target = "prescription"),
            @Mapping(source = "recordNumber", target = "recordNumber"),
            @Mapping(source = "appointment.appointmentNumber.value", target = "appointmentNumber"),
            @Mapping(source = "appointment.dateTime", target = "appointmentDateTime"),
            @Mapping(source = "appointment.consultationType", target = "consultationType"),
            @Mapping(source = "appointment.status", target = "status"),
            @Mapping(source = "appointment.patientId", target = "patientId"),
            @Mapping(source = "appointment.physicianNumber", target = "physicianNumber"),
    })
    AppointmentRecordView toAppointmentRecordView(AppointmentRecord record);

    List<AppointmentRecordView> toAppointmentRecordView(List<AppointmentRecord> records);
}



