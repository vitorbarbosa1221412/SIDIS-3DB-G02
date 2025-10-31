package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.api;

import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.dto.AppointmentDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.dto.PhysicianDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Patient;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services.*;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.services.UserService;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.Role;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.User;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.api.UserInternalDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority; // Usar GrantedAuthority

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final PatientViewMapper mapper;
    private final PatientIntegrationService integrationService;

    // NOVO: SERVIÇO DE UTILIZADORES INJETADO
    private final UserService userService;

    // =============================================================
    // NOVO ENDPOINT INTERNO PARA O AUTHSERVICE
    // Endpoint protegido por hasRole("INTERNAL_SERVICE") no SecurityConfig.
    // =============================================================
    @GetMapping("/internal/users/{username}")
    public ResponseEntity<UserInternalDTO> getUserDetailsForAuth(@PathVariable String username) {

        return userService.findByUsername(username)
                .map(user -> {
                    // O objeto 'user' implementa UserDetails; usamos getAuthorities().
                    Set<String> roles = user.getAuthorities().stream()
                            // Role implementa GrantedAuthority, e getAuthority() devolve a String ("PATIENT", "ADMIN", etc.)
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toSet());

                    UserInternalDTO dto = new UserInternalDTO(
                            user.getId().toString(),
                            user.getUsername(),
                            user.getPassword(), // Retorna o HASH da password!
                            roles
                    );
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PatientView> createPatient(
            @RequestPart("patient") String patientJson,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture,
            HttpServletRequest servletRequest) {

        System.out.println("Content-Type recebido: " + servletRequest.getContentType());

        CreatePatientRequest request;
        try {
            ObjectMapper mapper = new ObjectMapper();
            request = mapper.readValue(patientJson, CreatePatientRequest.class);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (profilePicture != null) {
            request.setProfilePicture(profilePicture);
        }

        Patient patient = patientService.createPatient(request);

        return new ResponseEntity<>(mapper.toPatientView(patient), HttpStatus.CREATED);
    }

    // ... (Restante código do Controller mantido)

    @GetMapping("/debug/ids")
    public ResponseEntity<List<Long>> getAllPatientIds() {
        List<Patient> patients = patientService.getAllPatients();
        if (patients == null || patients.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        List<Long> ids = patients.stream()
                .map(Patient::getId)
                .collect(Collectors.toList());
        return new ResponseEntity<>(ids, HttpStatus.OK);
    }

    @PostMapping(value = "/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientView> createPatientJson(@Valid @RequestBody CreatePatientRequest request) {
        Patient patient = patientService.createPatient(request);
        return new ResponseEntity<>(mapper.toPatientView(patient), HttpStatus.CREATED);
    }

    @PostMapping(value = "/testMultipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> testMultipart(
            @RequestPart("patient") String patientJson,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) {

        System.out.println("Paciente JSON: " + patientJson);
        System.out.println("Ficheiro: " + (profilePicture != null ? profilePicture.getOriginalFilename() : "Nenhum"));

        return ResponseEntity.ok("Recebido com sucesso");
    }



    @GetMapping("/id/{id}/profile")
    public ResponseEntity<PatientView> getPatientById(@PathVariable Long id) {
        ResponseEntity<Patient> response = patientService.getPatientById(id);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            return new ResponseEntity<>(response.getStatusCode());
        }
        return new ResponseEntity<>(mapper.toPatientView(response.getBody()), HttpStatus.OK);
    }

    @GetMapping("/number/{number}")
    public ResponseEntity<PatientView> getPatientByNumber(@PathVariable String number) {
        ResponseEntity<Patient> response = patientService.getPatientByNumber(number);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            return new ResponseEntity<>(response.getStatusCode());
        }
        return new ResponseEntity<>(mapper.toPatientView(response.getBody()), HttpStatus.OK);
    }

    @GetMapping("/name/{name}/profile")
    public ResponseEntity<List<PatientView>> searchPatientsByName(
            @PathVariable String name,
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(name = "limit", defaultValue = "10") int pageLimit) {

        Page page = new Page(pageNumber, pageLimit);

        List<Patient> patients;
        if (name != null) {
            patients = patientService.searchByPatientName(name, page);
        } else {
            patients = patientService.getAllPatients(); // este método não tem parâmetros na interface
        }

        if (patients == null || patients.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(mapper.toPatientViewList(patients), HttpStatus.OK);
    }


    @PutMapping("/updatePatient")
    public ResponseEntity<Void> updatePersonalData(@RequestBody @Valid UpdatePatientRequest request, Principal principal) {
        String username = principal.getName();
        String email = username.split(",")[1];
        patientService.updatePersonalData(email, request);
        return ResponseEntity.noContent().build();
    }


}
