package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client;

import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.dto.AppointmentDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.dto.AppointmentRecordDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.config.ExternalServiceConfig;
import com.example.psoft25_1221392_1211686_1220806_1211104.exceptions.ServiceUnavailableException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


@Slf4j
@Component
public class AppointmentClient {

    private final RestTemplate restTemplate;
    private final ExternalServiceConfig config;

    public AppointmentClient(RestTemplate restTemplate, ExternalServiceConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    /**
     * Get appointment history with Circuit Breaker, Retry, Timeout, and Bulkhead
     * All resilience patterns are applied via annotations
     */
    @CircuitBreaker(name = "appointmentService", fallbackMethod = "getAppointmentHistoryFallback")
    @Retry(name = "appointmentService")
    @Bulkhead(name = "appointmentService")
    public List<AppointmentDTO> getAppointmentHistory(String patientNumber) {
        log.info("Fetching appointment history for patient: {}", patientNumber);
        //String url = config.getAppointmentServiceUrl() + "/my-appointments";
        String url = "http://appointment-service-1:4000/api/appointments/my-appointments";
        
        try {
            AppointmentDTO[] appointments = restTemplate.getForObject(url, AppointmentDTO[].class);
            List<AppointmentDTO> result = appointments != null ? List.of(appointments) : List.of();
            log.info("Successfully fetched {} appointments for patient {}", result.size(), patientNumber);
            return result;
        } catch (RestClientException e) {
            log.error("Error calling Appointment Service for patient {}: {}", patientNumber, e.getMessage());
            throw e;
        }
    }

    /**
     * Fallback method when Circuit Breaker is open or service is unavailable
     * Throws ServiceUnavailableException to inform the caller that the service is down
     */
    public List<AppointmentDTO> getAppointmentHistoryFallback(String patientNumber, Exception e) {
        log.warn("Circuit Breaker OPEN or service unavailable. Throwing ServiceUnavailableException for patient: {}. Error: {}", 
            patientNumber, e.getMessage());
        throw new ServiceUnavailableException("Appointment Service", "getAppointmentHistory");
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

    /**
     * Get appointment records with Circuit Breaker, Retry, Timeout, and Bulkhead
     */
    @CircuitBreaker(name = "appointmentService", fallbackMethod = "getAppointmentRecordsFallback")
    @Retry(name = "appointmentService")
    @Bulkhead(name = "appointmentService")
    public List<AppointmentRecordDTO> getAppointmentRecords(String patientNumber) {
        log.info("Fetching appointment records for patient: {}", patientNumber);
        String url = "http://localhost:4000/api" + "/appointmentRecords/patient/" + patientNumber;
        
        try {
            AppointmentRecordDTO[] records = restTemplate.getForObject(url, AppointmentRecordDTO[].class);
            List<AppointmentRecordDTO> result = records != null ? List.of(records) : List.of();
            log.info("Successfully fetched {} appointment records for patient {}", result.size(), patientNumber);
            return result;
        } catch (RestClientException e) {
            log.error("Error calling Appointment Records Service for patient {}: {}", patientNumber, e.getMessage());
            throw e; // Let Resilience4j handle retry/circuit breaker
        }
    }

    /**
     * Fallback method for appointment records
     * Throws ServiceUnavailableException to inform the caller that the service is down
     */
    public List<AppointmentRecordDTO> getAppointmentRecordsFallback(String patientNumber, Exception e) {
        log.warn("Circuit Breaker OPEN or service unavailable. Throwing ServiceUnavailableException for appointment records, patient: {}. Error: {}", 
            patientNumber, e.getMessage());
        throw new ServiceUnavailableException("Appointment Records Service", "getAppointmentRecords");
    }
}
