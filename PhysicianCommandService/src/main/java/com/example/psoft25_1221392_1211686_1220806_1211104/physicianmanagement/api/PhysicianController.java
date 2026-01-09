package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.api;


import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.CommandDispatcher;



import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.commands.CreatePhysicianCommand;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.commands.UpdatePhysicianCommand;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.commands.AssignPatientToPhysicianCommand;

import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.client.PatientClient;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.services.CreatePhysicianRequest;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.services.UpdatePhysicianRequest;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("api/physicians")
public class PhysicianController {


    @Autowired
    private CommandDispatcher commandDispatcher;




    @Autowired
    private PatientClient patientClient;
    @Autowired
    private UserService userService;


    // ENDPOINTS DE ESCRITA


    @Operation(summary = "Create an Physician")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createPhysician(
            @RequestPart("physician") @Valid final CreatePhysicianRequest request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        CreatePhysicianCommand command = new CreatePhysicianCommand(request, imageFile);
        commandDispatcher.dispatch(command);

        // Retorna 201 CREATED.
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{physicianNumber}")
    public ResponseEntity<?> updatePhysician(
            @PathVariable String physicianNumber,
            @Valid @RequestBody UpdatePhysicianRequest request) {

        UpdatePhysicianCommand command = new UpdatePhysicianCommand(physicianNumber, request);
        commandDispatcher.dispatch(command);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Assign a Patient to a Physician")
    @GetMapping("/assign/{physicianId}/{patientId}")
    // Nota: Mantido aqui porque apesar de ser GET, executa um COMANDO (altera estado)
    public ResponseEntity<String> assignPatientToPhysician(
            @PathVariable Long physicianId,
            @PathVariable Long patientId) {

        AssignPatientToPhysicianCommand command = new AssignPatientToPhysicianCommand(physicianId, patientId);
        commandDispatcher.dispatch(command);

        return ResponseEntity.ok("Patient assignment command dispatched successfully");
    }



    @GetMapping("/internal/users/{username}")
    public ResponseEntity<UserInternalDTO> getUserDetailsForAuth(@PathVariable String username) {
        // Precisamos disto aqui porque os Users estão na base de dados de escrita (PostgreSQL)
        return userService.findByUsername(username)
                .map(user -> {
                    Set<String> roles = user.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toSet());

                    UserInternalDTO dto = new UserInternalDTO(
                            user.getId().toString(),
                            user.getUsername(),
                            user.getPassword(),
                            roles
                    );
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/test-patient/{id}")
    public ResponseEntity<String> testPatientConnection(@PathVariable Long id) {
        boolean exists = patientClient.patientExists(id);
        return ResponseEntity.ok("Patient " + id + " exists? " + exists);
    }
}