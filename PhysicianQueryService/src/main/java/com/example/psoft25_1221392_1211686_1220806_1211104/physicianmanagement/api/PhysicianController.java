package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.api;


import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.QueryDispatcher;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.queries.GetPhysicianByIdQuery;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.queries.GetPhysicianByNumberQuery;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.queries.GetPhysicianWorkingHoursQuery;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.queries.SearchPhysiciansQuery;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.client.PatientClient;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/physicians")
public class PhysicianController {


    @Autowired
    private QueryDispatcher queryDispatcher;



    // Cliente externo
    @Autowired
    private PatientClient patientClient;

    @Operation(summary = "Get a Physician by his Id")
    @GetMapping("/{id}")
    public ResponseEntity<PhysicianDTO> getPhysicianById(@PathVariable Long id) {
        GetPhysicianByIdQuery query = new GetPhysicianByIdQuery(id);
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


    // ENDPOINTS DE TESTE


    @GetMapping("/test-patient/{id}")
    public ResponseEntity<String> testPatientConnection(@PathVariable Long id) {
        boolean exists = patientClient.patientExists(id);
        return ResponseEntity.ok("Patient " + id + " exists? " + exists);
    }


}