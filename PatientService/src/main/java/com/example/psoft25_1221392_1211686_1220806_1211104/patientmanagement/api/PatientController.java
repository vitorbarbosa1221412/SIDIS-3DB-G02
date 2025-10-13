package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.api;

import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Patient;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services.*;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.User;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final PatientViewMapper mapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PatientView> createPatient(
            @Valid @RequestPart("patient") CreatePatientRequest request,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture,
            HttpServletRequest servletRequest) {

        System.out.println("Content-Type recebido: " + servletRequest.getContentType());

        if (profilePicture != null) {
            request.setProfilePicture(profilePicture);
        }

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
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id);
    }

    @GetMapping("/number/{number}")
    public ResponseEntity<Patient> getPatientByNumber(@PathVariable String number) {
        return patientService.getPatientByNumber(number);
    }

    @GetMapping("/name/{name}/profile")
    public ResponseEntity<List<Patient>> searchPatientsByName(
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
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }


    @PutMapping("/updatePatient")
    public ResponseEntity<Void> updatePersonalData(@RequestBody @Valid UpdatePatientRequest request, Principal principal) {
        String username = principal.getName();
        String email = username.split(",")[1];
        patientService.updatePersonalData(email, request);
        return ResponseEntity.noContent().build();
    }





}
