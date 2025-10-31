package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client;

import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.dto.AppointmentDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.dto.AppointmentRecordDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.config.ExternalServiceConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


@Component
public class AppointmentClient {

    private final RestTemplate restTemplate;
    private final ExternalServiceConfig config;

    public AppointmentClient(RestTemplate restTemplate, ExternalServiceConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }


    public List<AppointmentDTO> getAppointmentHistory(String patientNumber) {
        try {
            //
            //

            String url = config.getAppointmentServiceUrl() + "/my-appointments";
            ResponseEntity<String> rawResponse = restTemplate.getForEntity(url, String.class);
            System.out.println("DEBUG - Raw JSON: " + rawResponse.getBody());
            System.out.println("DEBUG - Status: " + rawResponse.getStatusCode());
            AppointmentDTO[] appointments = restTemplate.getForObject(url, AppointmentDTO[].class);
            return appointments != null ? List.of(appointments) : List.of();


            // TEMPORARY: Return empty list for standalone execution
            //return List.of();
        } catch (Exception e) {
            // Log the error and return empty list
            System.err.println("Error calling Appointment Service: " + e.getMessage());
            return List.of();
        }
    }


    public List<AppointmentDTO> getUpcomingAppointments(String patientNumber) {
        try {
            //
            //

            String url = config.getAppointmentServiceUrl() + "/upcoming";
            //ResponseEntity<String> rawResponse = restTemplate.getForEntity(url, String.class);
            //System.out.println("RAW RESPONSE: " + rawResponse.getBody());
            AppointmentDTO[] appointments = restTemplate.getForObject(url, AppointmentDTO[].class);
            return appointments != null ? List.of(appointments) : List.of();


            //return List.of();
        } catch (Exception e) {
            System.err.println("Error calling Appointment Service: " + e.getMessage());
            return List.of();
        }
    }


    public Optional<AppointmentDTO> getAppointmentByNumber(String appointmentNumber) {
        try {
            //
            //

            String url = config.getAppointmentServiceUrl() + "/" + appointmentNumber;

            ResponseEntity<String> rawResponse = restTemplate.getForEntity(url, String.class);
            System.out.println("DEBUG - Raw JSON: " + rawResponse.getBody());
            System.out.println("DEBUG - Status: " + rawResponse.getStatusCode());

            AppointmentDTO appointment = restTemplate.getForObject(url, AppointmentDTO.class);
            return Optional.ofNullable(appointment);


            //return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error calling Appointment Service: " + e.getMessage());
            return Optional.empty();
        }
    }


    public Optional<AppointmentDTO> createAppointment(Object appointmentRequest) {
        try {
            //
            //
            /*
            String url = config.getAppointmentServiceUrl();
            AppointmentDTO appointment = restTemplate.postForObject(url, appointmentRequest, AppointmentDTO.class);
            return Optional.ofNullable(appointment);
            */

            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error calling Appointment Service: " + e.getMessage());
            return Optional.empty();
        }
    }


    public boolean cancelAppointment(String appointmentNumber) {
        try {
            //
            //

            String url = config.getAppointmentServiceUrl() + "/" + appointmentNumber + "/cancel";
            restTemplate.delete(url);
            return true;



            //return true;
        } catch (Exception e) {
            System.err.println("Error calling Appointment Service: " + e.getMessage());
            return false;
        }
    }

    public List<LocalTime> getAvailableSlots(String physicianNumber, LocalDate date) {
        try {
            String url = config.getAppointmentServiceUrl() + "/availableSlots?physicianNumber=" + physicianNumber + "&date=" + date;
            LocalTime[] slots = restTemplate.getForObject(url, LocalTime[].class);
            return slots != null ? List.of(slots) : List.of();
        } catch (Exception e) {
            System.err.println("Error getting available slots: " + e.getMessage());
            return List.of();
        }
    }

    public Optional<AppointmentDTO> scheduleAppointment(Object scheduleRequest) {
        try {
            String url = config.getAppointmentServiceUrl() + "/scheduleAppointment";
            AppointmentDTO appointment = restTemplate.postForObject(url, scheduleRequest, AppointmentDTO.class);
            return Optional.ofNullable(appointment);
        } catch (Exception e) {
            System.err.println("Error scheduling appointment: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<AppointmentDTO> searchAppointmentsByPhysician(String physicianName) {
        try {
            String url = config.getAppointmentServiceUrl() + "/search?physicianName=" + physicianName;
            AppointmentDTO[] appointments = restTemplate.getForObject(url, AppointmentDTO[].class);
            return appointments != null ? List.of(appointments) : List.of();
        } catch (Exception e) {
            System.err.println("Error searching appointments by physician: " + e.getMessage());
            return List.of();
        }
    }

    public List<AppointmentRecordDTO> getAppointmentRecords(String patientNumber) {
        try {
            String url = "http://localhost:4000/api" + "/appointmentRecords/patient/" + patientNumber;
            AppointmentRecordDTO[] records = restTemplate.getForObject(url, AppointmentRecordDTO[].class);
            return records != null ? List.of(records) : List.of();
        } catch (Exception e) {
            System.err.println("Error getting appointment records: " + e.getMessage());
            return List.of();
        }
    }
}
