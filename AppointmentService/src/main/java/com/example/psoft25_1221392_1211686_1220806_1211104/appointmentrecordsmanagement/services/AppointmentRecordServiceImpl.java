package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.services;

import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.Appointment;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.repositories.AppointmentRepository;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.api.AppointmentRecordView;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.api.AppointmentRecordViewMapper;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.api.ElectronicPrescriptionDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.model.AppointmentRecord;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.repositories.AppointmentRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentRecordServiceImpl implements AppointmentRecordService {

    @Autowired
    private AppointmentRecordRepository appointmentRecordRepository;

    @Autowired
    private AppointmentRecordMapper mapper;

    @Autowired
    private AppointmentRecordViewMapper appointmentRecordViewMapper;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private Long generateNextRecordNumber() {
        return appointmentRecordRepository.findMaxRecordNumber().orElse(1000L) + 1;
    }




    @Override
    public AppointmentRecordView createAppointmentRecord(CreateAppointmentRecordRequest request) {
        Appointment appointment = appointmentRepository
                .findByAppointmentNumber_Value(request.getAppointmentNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (appointmentRecordRepository.existsByAppointment(appointment)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Appointment already has a record");
        }

        AppointmentRecord record = new AppointmentRecord();
        record.setDiagnosis(request.getDiagnosis());
        record.setTreatmentRecommendation(request.getTreatmentRecommendation());
        record.setPrescription(request.getPrescription());
        record.setAppointment(appointment);

        Long nextRecordNumber = generateNextRecordNumber();
        record.setRecordNumber(nextRecordNumber);

        AppointmentRecord saved = appointmentRecordRepository.save(record);

        return appointmentRecordViewMapper.toAppointmentRecordView(saved);
    }




    @Override
    public AppointmentRecord getAppointmentRecordByRecordNumber(Long recordNumber) {
        List<AppointmentRecord> records = appointmentRecordRepository.getAppointmentRecordByRecordNumber(recordNumber);

        if (records.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment record not found");
        }

        return records.get(0);
    }

    @Override
    public List<AppointmentRecord> getAppointmentRecordsByPatientNumber(String patientNumber) {
        List<AppointmentRecord> filteredRecords = appointmentRecordRepository.findByAppointment_PatientId(patientNumber);

        if (filteredRecords.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No appointment records found for the patient");
        }

        return filteredRecords;
    }

    @Override
    public List<AppointmentRecord> getAllAppointmentRecords() {
        return appointmentRecordRepository.findAll();
    }

    @Override
    public AppointmentRecord updateAppointmentRecord(Long recordNumber, UpdateAppointmentRecordRequest request) {
        AppointmentRecord existingAppointmentRecord = appointmentRecordRepository
                .findByRecordNumber(recordNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AppointmentRecord not found"));

        mapper.update(request, existingAppointmentRecord);

        return appointmentRecordRepository.save(existingAppointmentRecord);
    }

    @Override
    public List<AppointmentRecord> searchAppointmentRecordByPatient(Page page, String patientNumber, Long recordNumber) {
        if (page == null) {
            page = new Page(1, 5);
        }

        List<AppointmentRecord> patientRecord = appointmentRecordRepository.searchByPatientIdAndRecordNumber(patientNumber, recordNumber);
        if (patientRecord.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient or Patient's Record not found");
        } else {
            return patientRecord;
        }
    }

    @Override
    public List<ElectronicPrescriptionDTO> generateElectronicPrescription(Long recordNumber) {
        List<Object[]> prescriptionData = appointmentRecordRepository
                .findElectronicPrescriptionParameters(recordNumber);

        if (prescriptionData.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Appointment Record not found");
        }

        List<ElectronicPrescriptionDTO> electronicPrescription = new ArrayList<>();

        for (Object[] row : prescriptionData) {
            String patientId = (String) row[0];
            String physicianNumber = (String) row[1];
            String prescription = (String) row[2];

            electronicPrescription.add(new ElectronicPrescriptionDTO(
                    patientId,
                    physicianNumber,
                    prescription
            ));
        }

        return electronicPrescription;

    }

    @Override
    public List<AppointmentRecordView> getMyAppointmentRecordsByPatientNumber(String patientNumber) {
        List<AppointmentRecord> records = appointmentRecordRepository.findByAppointment_PatientId(patientNumber);
        return appointmentRecordViewMapper.toAppointmentRecordView(records);

}}


