package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.api;

import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.Appointment;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.services.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("api/appointments")
public class AppointmentController {
    @Autowired
    @Qualifier("restTemplateWithAuth")
    private RestTemplate restTemplate;

    @Autowired
    private AppointmentService appointmentService;
//j
    @Autowired
    private AppointmentMapper appointmentMapper;

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@Valid @RequestBody CreateAppointmentRequest request) {
        Appointment created = appointmentService.createAppointment(request);
        return ResponseEntity.ok(created);
    }

//    @GetMapping("/patient/{patientId}")
//    public ResponseEntity<Appointment> getAppointmentsByPatient(@PathVariable Long patientNumber) {
//        return appointmentService.getAppointmentByPatientNumber(patientId);
//    }

    @PutMapping("/{appointmentNumber}")
    public ResponseEntity<Appointment> updateAppointment(
            @PathVariable String appointmentNumber,
            @Valid @RequestBody UpdateAppointmentRequest request) {
        Appointment updated = appointmentService.updateAppointment(appointmentNumber, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{appointmentNumber}")
    public ResponseEntity<Appointment> getAppointmentByNumber(@PathVariable String appointmentNumber) {
        return appointmentService.viewAppointmentByNumber(appointmentNumber);
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> list = appointmentService.getAllAppointments();
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{appointmentNumber}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable String appointmentNumber) {
        appointmentService.cancelByAppointmentNumber(appointmentNumber);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/availableSlots")
    public ResponseEntity<List<LocalTime>> getAvailableSlots(
            @RequestParam String physicianNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(appointmentService.getAvailableSlots(physicianNumber, date));
    }

    @PostMapping("/scheduleAppointment")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Appointment> scheduleAppointment(
            @AuthenticationPrincipal Jwt principal,
            @Valid @RequestBody ScheduleAppointmentRequest request
    ) {
        String userId = principal.getClaim("userId").toString();

        Appointment appointment = appointmentService.scheduleAppointmentByPatient(
                userId,
                request.getPhysicianNumber(),
                request.getDateTime(),
                request.getConsultationType()
        );

        return new ResponseEntity<>(appointment, HttpStatus.CREATED);
    }

    @GetMapping("/my-appointments")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<AppointmentView>> getMyAppointments(@AuthenticationPrincipal Jwt principal) {
        String userId = principal.getClaim("userId").toString();
        List<Appointment> history = appointmentService.getAppointmentHistory(userId);
        List<AppointmentView> views = appointmentMapper.toView(history);

        return ResponseEntity.ok(views);
    }

//    @GetMapping("/statistics/age-groups")
//    public ResponseEntity<List<AgeGroupStats>> getAppointmentStatisticsByAgeGroup() {
//        return ResponseEntity.ok(appointmentService.getAppointmentStatsByAgeGroup());
//    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Appointment>> getUpcomingAppointments() {
        List<Appointment> upcomingAppointments = appointmentService.getUpcomingAppointments();
        return ResponseEntity.ok(upcomingAppointments);
    }

//    @GetMapping("/average-duration")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<PhysicianAverageDurationDTO>> getAverageDurationPerPhysician() {
//        return ResponseEntity.ok(appointmentService.getAverageAppointmentDurationPerPhysician());
//    }

    @GetMapping("/monthly-report")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentServiceImpl.MonthlyAppointmentReport>> getMonthlyReport() {
        return ResponseEntity.ok(appointmentService.getMonthlyReport());
    }
}