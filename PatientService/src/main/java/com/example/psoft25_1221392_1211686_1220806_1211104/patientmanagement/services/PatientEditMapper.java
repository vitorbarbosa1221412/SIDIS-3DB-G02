package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services;

import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.HealthConcerns;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Patient;
//import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.ProfilePicture;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.Role;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.User;
import org.mapstruct.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {PatientEditMapper.HealthConcernsMapper.class})
public abstract class PatientEditMapper {

    @Mappings({
            //@Mapping(target = "profilePicture", source = "profilePicture", qualifiedByName = "multipartFileToProfilePicture"),
            @Mapping(target = "user", source = "request", qualifiedByName = "createUserFromRequest"),
            @Mapping(target = "id", ignore=true),
            @Mapping(target = "patientNumber"),
            @Mapping(target = "address"),
            @Mapping(target = "enabled", constant = "true"),
            @Mapping(target = "insuranceInformation"),
            @Mapping(target = "healthConcerns", source = "healthConcerns")
    })
    public abstract Patient create(CreatePatientRequest request);

//    @Named("multipartFileToProfilePicture")
//    protected ProfilePicture multipartFileToProfilePicture(MultipartFile file) {
//        if (file == null || file.isEmpty()) return null;
//        try {
//            return new ProfilePicture(
//                    file.getOriginalFilename(),
//                    file.getContentType(),
//                    file.getBytes()
//            );
//        } catch (IOException e) {
//            throw new RuntimeException("Erro ao converter imagem de perfil", e);
//        }
//    }

    @Named("createUserFromRequest")
    protected User createUserFromRequest(CreatePatientRequest request) {
        User user = new User(request.getEmailAddress(), request.getPassword());
        user.setFullName(request.getName());

        // Adiciona as authorities se forem fornecidas
        if (request.getAuthorities() != null) {
            for (String role : request.getAuthorities()) {
                user.addAuthority(new Role(role));
            }
        } else {
            user.addAuthority(new Role("ROLE_PATIENT"));
        }

        return user;
    }

    @AfterMapping
    protected void linkHealthConcerns(@MappingTarget Patient patient) {
        if (patient.getHealthConcerns() != null) {
            for (HealthConcerns hc : patient.getHealthConcerns()) {
                hc.setPatient(patient); // ligação inversa
            }
        }
    }

    @Mapper(componentModel = "spring")
    public interface HealthConcernsMapper {

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "patient", ignore = true)
        HealthConcerns toEntity(CreateHealthConcernsRequest dto);

        List<HealthConcerns> toEntityList(List<CreateHealthConcernsRequest> dtos);
    }
}





