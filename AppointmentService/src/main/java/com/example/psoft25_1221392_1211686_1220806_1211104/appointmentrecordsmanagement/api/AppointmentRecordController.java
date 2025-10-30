package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.api;

import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.model.AppointmentRecord;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.services.AppointmentRecordService;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.services.CreateAppointmentRecordRequest;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.services.UpdateAppointmentRecordRequest;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.services.Page;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/appointmentRecords")
public class AppointmentRecordController {

    @Autowired
    private AppointmentRecordService appointmentRecordService;

    @Operation(summary = "Create an Appointment Record")
    @PostMapping
    public ResponseEntity<AppointmentRecordView> createAppointmentRecord(@RequestBody CreateAppointmentRecordRequest request) {
        AppointmentRecordView view = appointmentRecordService.createAppointmentRecord(request);
        return new ResponseEntity<>(view, HttpStatus.CREATED);
    }


    @Operation(summary = "Get an Appointment Record by record number")
    @GetMapping("/record/{recordNumber}")
    public ResponseEntity<AppointmentRecord> getAppointmentRecordByRecordNumber(@PathVariable Long recordNumber) {
        AppointmentRecord record = appointmentRecordService.getAppointmentRecordByRecordNumber(recordNumber);
        return ResponseEntity.ok(record);
    }

    @Operation(summary = "Get Appointment Records by Patient Number")
    @GetMapping("/patient/{patientNumber}")
    public ResponseEntity<List<AppointmentRecord>> getAppointmentRecordsByPatientNumber(@PathVariable String patientNumber) {
        List<AppointmentRecord> records = appointmentRecordService.getAppointmentRecordsByPatientNumber(patientNumber);
        return ResponseEntity.ok(records);
    }

    @Operation(summary = "Get All Appointment Records")
    @GetMapping("/getAll")
    public ResponseEntity<List<AppointmentRecord>> getAllAppointmentRecords() {
        List<AppointmentRecord> records = appointmentRecordService.getAllAppointmentRecords();
        return ResponseEntity.ok(records);
    }

    @Operation(summary = "Update an Appointment Record")
    @PutMapping("/{recordNumber}")
    public ResponseEntity<AppointmentRecord> updateAppointmentRecord(@PathVariable Long recordNumber, @Valid @RequestBody final UpdateAppointmentRecordRequest request) {
        AppointmentRecord updated = appointmentRecordService.updateAppointmentRecord(recordNumber, request);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Search Appointment Records by Patient")
    @GetMapping("/search/{patientNumber}/{recordNumber}")
    public ResponseEntity<List<AppointmentRecord>> searchAppointmentRecordByPatient(@PathVariable String patientNumber,
                                                                                    @PathVariable Long recordNumber,
                                                                                    @RequestParam(name = "page", defaultValue = "1") int pageNumber,
                                                                                    @RequestParam(name = "limit", defaultValue = "10") int pageLimit) {

        Page page = new Page(pageNumber, pageLimit);

        List<AppointmentRecord> records;
        if (patientNumber == null || recordNumber == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            records = appointmentRecordService.searchAppointmentRecordByPatient(page, patientNumber, recordNumber);
        }

        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    @Operation(summary = "Generate Electronic Prescription")
    @GetMapping("/electronic-prescription/{recordNumber}")
    public ResponseEntity<List<ElectronicPrescriptionDTO>> generateElectronicPrescription(@PathVariable Long recordNumber) {
        List<ElectronicPrescriptionDTO> records = appointmentRecordService.generateElectronicPrescription(recordNumber);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/myRecords/{patientNumber}")
    public ResponseEntity<List<AppointmentRecordView>> getMyAppointmentRecordsByPatientNumber(@PathVariable String patientNumber) {
        List<AppointmentRecordView> records = appointmentRecordService.getMyAppointmentRecordsByPatientNumber(patientNumber);
        return ResponseEntity.ok(records);
    }




}




