package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.api;

import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.client.PatientClient;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.Physician;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.services.*;

// =========================================================================
// IMPORTS DO DOMÍNIO DE UTILIZADORES
// =========================================================================
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.services.UserService;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.Role;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.User;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.api.UserInternalDTO; // Importa o DTO que o AuthService espera
import org.springframework.security.core.GrantedAuthority;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.support.BeanDefinitionDsl; // REMOVIDO
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/physicians")
public class PhysicianController {
    @Autowired
    private PhysicianService physicianService;
    @Autowired
    private PatientClient patientClient;

    // NOVO: SERVIÇO DE UTILIZADORES INJETADO
    @Autowired
    private UserService userService;

    // =============================================================
    // ENDPOINT INTERNO CORRIGIDO (FORNECE CREDENCIAIS AO AUTHSERVICE)
    // =============================================================
    @GetMapping("/internal/users/{username}")
    public ResponseEntity<UserInternalDTO> getUserDetailsForAuth(@PathVariable String username) {

        return userService.findByUsername(username)
                .map(user -> {
                    // CORREÇÃO: User implementa UserDetails, por isso usamos getAuthorities().
                    // O método getAuthority() na classe Role devolve a String da função.
                    Set<String> roles = user.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toSet());

                    UserInternalDTO dto = new UserInternalDTO(
                            user.getId().toString(),
                            user.getUsername(),
                            user.getPassword(), // HASH da password!
                            roles
                    );
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create an Physician")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Physician> createPhysician(
            @RequestPart("physician") @Valid final CreatePhysicianRequest request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        return new ResponseEntity<>(physicianService.createPhysician(request, imageFile), HttpStatus.CREATED);
    }

    @PutMapping("/{physicianNumber}")
    public ResponseEntity<Physician> updatePhysician(
            @PathVariable String physicianNumber,
            @Valid @RequestBody UpdatePhysicianRequest request) {
        Physician updated = physicianService.updatePhysician(physicianNumber, request);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get a Physician by his Id")
    @GetMapping("/{id}")
    public ResponseEntity<Physician> getPhysicianById(@PathVariable Long id) {
        return physicianService.getPhysicianById(id);
    }

    @Operation(summary = "Get a Physician by his number")
    @GetMapping("/number/{number}")
    public ResponseEntity<Physician> getPhysicianByNumber(@PathVariable String number) {
        return physicianService.getPhysicianByNumber(number);
    }

    @Operation(summary = "Get the Working Hours of a Physician by his Id")
    @GetMapping("/workinghours/{id}")
    public ResponseEntity<String> getPhysicianWorkingHoursById(@PathVariable Long id) {
        return physicianService.getPhysicianWorkingHoursById(id);
    }

    @GetMapping
    public ResponseEntity<List<Physician>> searchPhysicians(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "specialty", required = false) String specialty,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        Page p = new Page(page, limit);

        if (name != null && specialty != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (name != null) {
            return physicianService.searchPhysiciansByName(p, name);
        } else if (specialty != null) {
            return physicianService.searchPhysiciansBySpecialty(p, specialty);

        } else {
            return new ResponseEntity<>(physicianService.getAllPhysicians(p), HttpStatus.OK);
        }
    }

   /* @GetMapping("/top5physicians")
    public ResponseEntity<String> searchTop5PhysiciansAsString() {
        List<Physician> top5physicians = physicianService.searchTop5Physicians();

        if (top5physicians.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum médico encontrado.");
        }

        StringBuilder response = new StringBuilder("Os 5 médicos com mais consultas são:\n");
        for (int i = 0; i < top5physicians.size(); i++) {
            Physician p = top5physicians.get(i);
            response.append((i + 1)).append(". ").append(p.getName()).append("\n");
        }

        return ResponseEntity.ok(response.toString());
    } */

    // associar paciente a médico
    @Operation(summary = "Assign a Patient to a Physician")
    @GetMapping("/assign/{physicianId}/{patientId}")
    public ResponseEntity<String> assignPatientToPhysician(
            @PathVariable Long physicianId,
            @PathVariable Long patientId) {

        physicianService.assignPatientToPhysician(physicianId, patientId);
        return ResponseEntity.ok("Patient assigned successfully");
    }
    @GetMapping("/test-patient/{id}")
    public ResponseEntity<String> testPatientConnection(@PathVariable Long id) {
        boolean exists = patientClient.patientExists(id);
        return ResponseEntity.ok("Patient " + id + " exists? " + exists);
    }



}