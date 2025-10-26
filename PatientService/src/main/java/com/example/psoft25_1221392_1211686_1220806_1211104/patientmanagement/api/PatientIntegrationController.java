package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.api;

import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.dto.AppointmentDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.dto.PhysicianDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Patient;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.repositories.PatientRepository;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services.PatientIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/patients/integration")
@RequiredArgsConstructor
public class PatientIntegrationController {

    private final PatientIntegrationService integrationService;
    private final PatientRepository patientRepository;

    // ===== INTEGRATION ENDPOINTS WITH OTHER SERVICES =====
    

    @GetMapping("/appointments/my-appointments")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<AppointmentDTO>> getMyAppointments(@AuthenticationPrincipal Jwt principal) {
        try {
            Long userId = principal.getClaim("userId");
            
            Patient patient = patientRepository.findByUser_Id(userId)
                    .orElse(null);
            if (patient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            List<AppointmentDTO> appointments = integrationService.getAppointmentHistory(patient.getPatientNumber());
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/appointments/upcoming")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<AppointmentDTO>> getUpcomingAppointments(@AuthenticationPrincipal Jwt principal) {
        try {
            Long userId = principal.getClaim("userId");
            
            Patient patient = patientRepository.findByUser_Id(userId)
                    .orElse(null);
            if (patient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            List<AppointmentDTO> appointments = integrationService.getUpcomingAppointments(patient.getPatientNumber());
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    


    @GetMapping("/physicians/id/{physicianId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<PhysicianDTO> getPhysicianById(@PathVariable Long physicianId) {
        Optional<PhysicianDTO> physician = integrationService.getPhysicianById(physicianId);
        return physician.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }




    @GetMapping("/physicians")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<List<PhysicianDTO>> searchPhysicians(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "specialty", required = false) String specialty
    ) {
        if (name != null && specialty != null) {
            return ResponseEntity.badRequest().build();
        }
        if (name != null) {
            return ResponseEntity.ok(integrationService.searchPhysiciansByName(name));
        }
        if (specialty != null) {
            return ResponseEntity.ok(integrationService.searchPhysiciansBySpecialty(specialty));
        }
        return ResponseEntity.ok(List.of());
    }



    // ===== APPOINTMENT OPERATIONS =====

    @GetMapping("/appointments/availableSlots")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<List<LocalTime>> getAvailableSlots(
            @RequestParam String physicianNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        try {
            List<LocalTime> slots = integrationService.getAvailableSlots(physicianNumber, date);
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/appointments/scheduleAppointment")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentDTO> scheduleAppointment(
            @AuthenticationPrincipal Jwt principal,
            @RequestBody @jakarta.validation.Valid ScheduleAppointmentRequest request
    ) {
        try {
            Long userId = principal.getClaim("userId");

            Patient patient = patientRepository.findByUser_Id(userId)
                    .orElse(null);
            if (patient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Get available slots for the physician on that date
            List<LocalTime> availableSlots = integrationService.getAvailableSlots(
                    request.getPhysicianNumber(), 
                    request.getDateTime().toLocalDate()
            );
            
            // Check if the requested time slot is available
            boolean available = availableSlots.contains(request.getDateTime().toLocalTime());
            if (!available) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            return integrationService.scheduleAppointment(request)
                    .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/appointments/search")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<AppointmentDTO>> searchMyAppointmentsByPhysician(
            @AuthenticationPrincipal Jwt principal,
            @RequestParam String physicianName
    ) {
        try {
            Long userId = principal.getClaim("userId");
            Patient patient = patientRepository.findByUser_Id(userId).orElse(null);
            if (patient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            List<AppointmentDTO> list = integrationService.searchAppointmentsByPhysician(physicianName);
            List<AppointmentDTO> mine = list.stream()
                    .filter(a -> patient.getPatientNumber().equals(a.getPatientNumber()))
                    .toList();
            return ResponseEntity.ok(mine);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/appointments/{appointmentNumber}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<AppointmentDTO> getAppointmentByNumber(@PathVariable String appointmentNumber) {
        Optional<AppointmentDTO> appointment = integrationService.getAppointmentByNumber(appointmentNumber);
        return appointment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/appointments/{appointmentNumber}/cancel")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<Void> cancelAppointment(@PathVariable String appointmentNumber) {
        boolean success = integrationService.cancelAppointment(appointmentNumber);
        return success ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }

}



