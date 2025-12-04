package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.api;

// IMPORTS DO CQRS CORE
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.CommandDispatcher;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.QueryDispatcher;

// IMPORTS DOS COMANDOS
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.commands.CreatePhysicianCommand;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.commands.UpdatePhysicianCommand;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.commands.AssignPatientToPhysicianCommand;

// IMPORTS DAS QUERIES
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.queries.GetPhysicianByIdQuery;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.queries.GetPhysicianByNumberQuery;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.queries.GetPhysicianWorkingHoursQuery;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.queries.SearchPhysiciansQuery;


// OUTROS IMPORTS
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.client.PatientClient;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.services.CreatePhysicianRequest;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.services.UpdatePhysicianRequest;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("api/physicians")
public class PhysicianController {

    // REMOVIDO: private PhysicianService physicianService;

    // NOVO: Dependências CQRS
    @Autowired
    private CommandDispatcher commandDispatcher;
    @Autowired
    private QueryDispatcher queryDispatcher;

    // Outras dependências mantidas
    @Autowired
    private PatientClient patientClient;
    @Autowired
    private UserService userService;


    // =========================================================================
    // ENDPOINTS DE COMANDO (ESCRITA - POST, PUT, DELETE)
    // =========================================================================

    @Operation(summary = "Create an Physician")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createPhysician( // Retorna ResponseEntity<?> pois o Command não deve retornar a entidade
                                              @RequestPart("physician") @Valid final CreatePhysicianRequest request,
                                              @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        // Mapeamento para o Command
        CreatePhysicianCommand command = new CreatePhysicianCommand(request, imageFile);

        // Despacho do Command
        commandDispatcher.dispatch(command);

        // Retorna 201 CREATED. A consulta subsequente usará o Read DB.
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{physicianNumber}")
    public ResponseEntity<?> updatePhysician( // Retorna ResponseEntity<?>
                                              @PathVariable String physicianNumber,
                                              @Valid @RequestBody UpdatePhysicianRequest request) {

        // Mapeamento para o Command
        UpdatePhysicianCommand command = new UpdatePhysicianCommand(physicianNumber, request);

        // Despacho do Command
        commandDispatcher.dispatch(command);

        // Retorna 200 OK (ou 204 No Content)
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Assign a Patient to a Physician")
    @GetMapping("/assign/{physicianId}/{patientId}") // Atenção: PUT seria mais idiomático para 'assign'
    public ResponseEntity<String> assignPatientToPhysician(
            @PathVariable Long physicianId,
            @PathVariable Long patientId) {

        // Mapeamento para o Command
        AssignPatientToPhysicianCommand command = new AssignPatientToPhysicianCommand(physicianId, patientId);

        // Despacho do Command
        commandDispatcher.dispatch(command);

        return ResponseEntity.ok("Patient assignment command dispatched successfully");
    }

    // =========================================================================
    // ENDPOINTS DE CONSULTA (LEITURA - GET)
    // =========================================================================

    @Operation(summary = "Get a Physician by his Id")
    @GetMapping("/{id}")
    public ResponseEntity<PhysicianDTO> getPhysicianById(@PathVariable Long id) {
        // 1. Mapeamento para a Query
        GetPhysicianByIdQuery query = new GetPhysicianByIdQuery(id);

        // 2. Despacho da Query (acessa MongoDB e retorna PhysicianDTO)
        Optional<PhysicianDTO> physicianDTO = queryDispatcher.dispatch(query);

        return physicianDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get a Physician by his number")
    @GetMapping("/number/{number}")
    public ResponseEntity<PhysicianDTO> getPhysicianByNumber(@PathVariable String number) {

        GetPhysicianByNumberQuery query = new GetPhysicianByNumberQuery(number);
        Optional<PhysicianDTO> physicianDTO = queryDispatcher.dispatch(query);

        return physicianDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get the Working Hours of a Physician by his Id")
    @GetMapping("/workinghours/{physicianNumber}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<String> getPhysicianWorkingHoursByPhysicianNumber(@PathVariable String physicianNumber) {

        GetPhysicianWorkingHoursQuery query = new GetPhysicianWorkingHoursQuery(physicianNumber);
        // Assumimos que esta Query retorna a String das horas, se existir
        String workingHours = queryDispatcher.dispatch(query);

        if (workingHours != null) {
            return ResponseEntity.ok(workingHours);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PhysicianDTO>> searchPhysicians(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "specialty", required = false) String specialty,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        if (name != null && specialty != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        SearchPhysiciansQuery query = new SearchPhysiciansQuery(name, specialty, page, limit);
        List<PhysicianDTO> results = queryDispatcher.dispatch(query);

        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    // =========================================================================
    // ENDPOINTS NÃO-CQRS E LEGADO (MANTIDOS)
    // =========================================================================

    @GetMapping("/internal/users/{username}")
    public ResponseEntity<UserInternalDTO> getUserDetailsForAuth(@PathVariable String username) {
        // ... (lógica mantida) ...
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

    @GetMapping("/test-patient/{id}")
    public ResponseEntity<String> testPatientConnection(@PathVariable Long id) {
        boolean exists = patientClient.patientExists(id);
        return ResponseEntity.ok("Patient " + id + " exists? " + exists);
    }
}