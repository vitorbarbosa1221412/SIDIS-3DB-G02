package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client;

import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.dto.PhysicianDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.config.ExternalServiceConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;


@Component
public class PhysicianClient {

    private final RestTemplate restTemplate;
    private final ExternalServiceConfig config;

    public PhysicianClient(RestTemplate restTemplate, ExternalServiceConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    


    public Optional<PhysicianDTO> getPhysicianById(Long physicianId) {
        try {


            String url = config.getPhysicianServiceUrl() + "/" + physicianId;
            PhysicianDTO physician = restTemplate.getForObject(url, PhysicianDTO.class);
            return Optional.ofNullable(physician);

            

            //return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error calling Physician Service: " + e.getMessage());
            return Optional.empty();
        }
    }


    public boolean isPhysicianAvailable(String physicianNumber, String dateTime) {
        try {

            String url = config.getPhysicianServiceUrl() + "/" + physicianNumber + "/availability";
            AvailabilityRequest request = new AvailabilityRequest(dateTime);
            Boolean available = restTemplate.postForObject(url, request, Boolean.class);
            return available != null && available;

            //not done ...... check later

            //return true;
        } catch (Exception e) {
            System.err.println("Error checking physician availability: " + e.getMessage());
            return false;
        }
    }

    public Optional<PhysicianDTO[]> searchPhysiciansByName(String name) {
        try {
            String url = config.getPhysicianServiceUrl() + "?name=" + name;
            PhysicianDTO[] physicians = restTemplate.getForObject(url, PhysicianDTO[].class);
            return Optional.ofNullable(physicians);
        } catch (Exception e) {
            System.err.println("Error searching physicians by name: " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<PhysicianDTO[]> searchPhysiciansBySpecialty(String specialty) {
        try {
            String url = config.getPhysicianServiceUrl() + "?specialty=" + specialty;
            PhysicianDTO[] physicians = restTemplate.getForObject(url, PhysicianDTO[].class);
            return Optional.ofNullable(physicians);
        } catch (Exception e) {
            System.err.println("Error searching physicians by specialty: " + e.getMessage());
            return Optional.empty();
        }
    }
}
