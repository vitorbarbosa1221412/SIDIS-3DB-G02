package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.services;

import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.api.AppointmentRecordView;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.api.ElectronicPrescriptionDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.model.AppointmentRecord;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Patient;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AppointmentRecordService {

    AppointmentRecordView createAppointmentRecord(CreateAppointmentRecordRequest request);

    AppointmentRecord getAppointmentRecordByRecordNumber(Long recordNumber);

    List<AppointmentRecord> getAppointmentRecordsByPatientNumber(String patientNumber);

    List<AppointmentRecord> getAllAppointmentRecords();

    AppointmentRecord updateAppointmentRecord(Long recordNumber, UpdateAppointmentRecordRequest request);

    List<AppointmentRecord> searchAppointmentRecordByPatient(Page page, String patientNumber, Long recordNumber);

    List<ElectronicPrescriptionDTO> generateElectronicPrescription(Long recordNumber);

    List<AppointmentRecordView> getMyAppointmentRecordsByPatientNumber(String patientNumber);

}

