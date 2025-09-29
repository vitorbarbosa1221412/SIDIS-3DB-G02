// patientmanagement/api/PatientViewMapper.java
package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.api;

import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class PatientViewMapper {
    @Mapping(source = "user.username", target = "username")
    public abstract PatientView toPatientView(Patient patient);

    public abstract List<PatientView> toPatientViewList(List<Patient> patients);
}
