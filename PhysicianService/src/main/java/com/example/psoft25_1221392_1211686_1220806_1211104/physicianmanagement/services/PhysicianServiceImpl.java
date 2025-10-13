package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.services;

import com.example.psoft25_1221392_1211686_1220806_1211104.exceptions.NotFoundException;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.client.PatientClient;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.Physician;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.PhysicianNumber;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.ProfilePicture;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.repositories.PhysicianRepository;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.Role;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.User;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.repositories.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PhysicianServiceImpl implements PhysicianService {

    private final PhysicianRepository physicianRepo;
    private final PhysicianEditMapper mapper;
    private final UserRepository userRepo;
    private final PatientClient patientClient;

    private final PhysicianNumber physicianNumber = new PhysicianNumber();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private String nameVal;

    public PhysicianServiceImpl(
            PhysicianRepository physicianRepo,
            PhysicianEditMapper mapper,
            UserRepository userRepo,
            PatientClient patientClient
    ) {
        this.physicianRepo = physicianRepo;
        this.mapper = mapper;
        this.userRepo = userRepo;
        this.patientClient = patientClient;
    }

    @Override
    public Physician createPhysician(CreatePhysicianRequest request, MultipartFile imageFile) {
        final Physician physician = mapper.create(request);

        physician.setPhysicianNumber(
                physicianNumber.generate(
                        physicianNumber.getNextPhysicianNumber(physicianRepo)
                )
        );

        User user = User.newUser(
                physician.getEmailAddress(),
                passwordEncoder.encode(request.getPassword()),
                physician.getName(),
                Role.PHYSICIAN
        );
        userRepo.save(user);
        physician.setUser(user);

        // Lógica para imagem
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                var profilePicture = new ProfilePicture(
                        imageFile.getOriginalFilename(),
                        imageFile.getContentType(),
                        imageFile.getBytes()
                );
                physician.setProfilePicture(profilePicture);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao processar a imagem de perfil", e);
            }
        }

        return physicianRepo.save(physician);
    }

    @Override
    public ResponseEntity<Physician> getPhysicianById(Long physicianId) {
        Physician physician = physicianRepo.findById(physicianId).orElse(null);
        if (physician == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Physician not found");
        } else {
            return new ResponseEntity<>(physician, HttpStatus.OK);
        }
    }

    @Override
    public List<Physician> getAllPhysicians(Page page) {
        Pageable pageable = PageRequest.of(page.getNumber() - 1, page.getLimit());
        return physicianRepo.findAll(pageable).getContent();
    }

    @Override
    public ResponseEntity<List<Physician>> searchPhysiciansByName(Page page, String name) {
        if (page == null) {
            page = new Page(1, 5);
        }

        List<Physician> physicians = physicianRepo.findByNameContaining(name);
        if (physicians.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Physician not found");
        } else {
            return new ResponseEntity<>(physicians, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<List<Physician>> searchPhysiciansBySpecialty(Page page, String specialty) {
        if (page == null) {
            page = new Page(1, 5);
        }

        List<Physician> physicians = physicianRepo.findBySpecialtyContaining(specialty);

        if (physicians.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Physician not found");
        } else {
            return new ResponseEntity<>(physicians, HttpStatus.OK);
        }
    }

    @Override
    public Physician updatePhysician(String physicianNumber, UpdatePhysicianRequest request) {
        Physician existingPhysician = physicianRepo
                .findByPhysicianNumber(physicianNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Physician not found"));

        mapper.update(request, existingPhysician);

        return physicianRepo.save(existingPhysician);
    }

   /* @Override
    public List<Physician> searchTop5Physicians() {
        return appointmentRepo.getTop5Physicians(PageRequest.of(0, 5));
    }*/

    // associar paciente a médico
    @Override
    public void assignPatientToPhysician(Long physicianId, Long patientId) {
        // Verifica se o paciente existe no PatientService
        if (!patientClient.patientExists(patientId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient ID " + patientId + " does not exist");
        }

        //Busca o medico
        Physician physician = physicianRepo.findById(physicianId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Physician not found"));


        System.out.println("Patient " + patientId + " assigned to physician " + physicianId);
    }
}




