package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.services;

import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.Appointment;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.repositories.AppointmentRepository;
import com.example.psoft25_1221392_1211686_1220806_1211104.exceptions.NotFoundException;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.Physician;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.PhysicianNumber;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.ProfilePicture;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.repositories.PhysicianRepository;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.Role;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.User;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Optional;

@Service
public class PhysicianServiceImpl implements PhysicianService {

    @Autowired
    private PhysicianRepository physicianRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private PhysicianEditMapper mapper;

    private final PhysicianNumber physicianNumber = new PhysicianNumber();

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private String nameVal;

    private final UserRepository userRepo;

    public PhysicianServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
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
    public ResponseEntity<List<Physician>> searchPhysiciansByName(Page page,String name) {
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

    @Override
    public List<Physician> searchTop5Physicians() {
        return appointmentRepo.getTop5Physicians(PageRequest.of(0, 5));
    }
}
