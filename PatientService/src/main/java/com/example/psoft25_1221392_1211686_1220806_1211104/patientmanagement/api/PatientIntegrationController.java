package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.api;

import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.dto.AppointmentDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.dto.PhysicianDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services.PatientIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/patients/integration")
@RequiredArgsConstructor
public class PatientIntegrationController {

    private final PatientIntegrationService integrationService;

    // ===== INTEGRATION ENDPOINTS WITH OTHER SERVICES =====
    

    @GetMapping("/{patientNumber}/appointments/history")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentHistory(@PathVariable String patientNumber) {
        List<AppointmentDTO> appointments = integrationService.getAppointmentHistory(patientNumber);
        return ResponseEntity.ok(appointments);
    }


    @GetMapping("/{patientNumber}/appointments/upcoming")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentDTO>> getUpcomingAppointments(@PathVariable String patientNumber) {
        List<AppointmentDTO> appointments = integrationService.getUpcomingAppointments(patientNumber);
        return ResponseEntity.ok(appointments);
    }


    


    @GetMapping("/physicians/id/{physicianId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<PhysicianDTO> getPhysicianById(@PathVariable Long physicianId) {
        Optional<PhysicianDTO> physician = integrationService.getPhysicianById(physicianId);
        return physician.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/physicians/{physicianNumber}/availability")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<Boolean> checkPhysicianAvailability(
            @PathVariable String physicianNumber,
            @RequestParam String dateTime) {
        boolean available = integrationService.isPhysicianAvailable(physicianNumber, dateTime);
        return ResponseEntity.ok(available);
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

