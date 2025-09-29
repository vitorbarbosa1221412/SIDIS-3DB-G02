package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.infrastructure;

import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.model.AppointmentRecord;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.services.Page;

import java.util.List;

 interface AppointmentRecordRepoCustom {
    List<AppointmentRecord> getAppointmentRecordByPatientNumber(Page page, Long patientNumber);
    List<AppointmentRecord> getAppointmentRecordByRecordNumber(Page page, Long recordNumber);
}

