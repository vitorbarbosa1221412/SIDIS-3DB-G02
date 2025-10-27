package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.services;

import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.api.PhysicianAverageDurationDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.infrastructure.repositories.impl.SpringDataAppointmentRepository;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.Appointment;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.AppointmentNumber;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.AppointmentStatus;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.ConsultationType;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.repositories.AppointmentRepository;
import com.example.psoft25_1221392_1211686_1220806_1211104.exceptions.NotFoundException;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.AgeGroupStats;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Patient;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.Physician;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    @Qualifier("restTemplateWithAuth")
    private RestTemplate restTemplate;

    @Value("${patient.port}")
    private int patientPort;

    @Value("${physician.port}")
    private int physicianPort;

    @Value("${PHYSICIAN_SERVICE_URL}")
    private String url_physician;

    @Value("${PATIENT_SERVICE_URL}")
    private String url_patient;

    @Value("${SERVER_URL}")
    private String serverUrl;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private SpringDataAppointmentRepository springDataAppointmentRepository;

    @Autowired
    private AppointmentEditMapper mapper;

    @Override
    public Appointment createAppointment(CreateAppointmentRequest request) {
        Appointment appointment = mapper.create(request);
        return appointmentRepository.save(appointment);
    }

    /*@Override
    public ResponseEntity<Appointment> getAppointmentByPatientId(Long patientId) {
        Appointment appointment = appointmentRepositorysitory.findById(appointmentId).orElse(null);
        if (appointment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found");
        } else {
            return new ResponseEntity<>(appointment, HttpStatus.OK);
        }
    }*/

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    public Appointment updateAppointment(String appointmentNumber, UpdateAppointmentRequest request) {
        Appointment existingAppointment = appointmentRepository
                .findByAppointmentNumber(new AppointmentNumber(appointmentNumber))

                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (request.getDateTime() != null) {
            existingAppointment.setDateTime(request.getDateTime());
            existingAppointment.setStatus(AppointmentStatus.RESCHEDULED);
        }

        return appointmentRepository.save(existingAppointment);
    }

    public ResponseEntity<Appointment> viewAppointmentByNumber(String appointmentNumber) {
        Appointment appointment = appointmentRepository.findByAppointmentNumber(new AppointmentNumber(appointmentNumber))
                .orElse(null);
        if (appointment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found");
        } else {
            return new ResponseEntity<>(appointment, HttpStatus.OK);
        }
    }
    @Override
    public void cancelByAppointmentNumber(String appointmentNumber) {
        Appointment appt = appointmentRepository
                .findByAppointmentNumber(new AppointmentNumber(appointmentNumber))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Appointment not found"));
        appt.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appt);
    }

    @Override
    public List<LocalTime> getAvailableSlots(String physicianNumber, LocalDate date) {
        String physicianUrl = serverUrl + physicianPort + url_physician + "number/" + physicianNumber;
        Physician physician = restTemplate.getForObject(physicianUrl, Physician.class);

        // 1. Obter o médico
        // Physician physician = physicianRepository.findByPhysicianNumber(physicianNumber)
        //        .orElseThrow(() -> new NotFoundException("Physician with number " + physicianNumber + " not found"));

        // 2. Interpretar workingHours no formato "HH:mm-HH:mm"
        String[] parts = physician.getWorkingHours().split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid workingHours format. Expected format: HH:mm-HH:mm");
        }

        LocalTime start = LocalTime.parse(parts[0].trim());
        LocalTime end = LocalTime.parse(parts[1].trim());

        // 3. Gerar slots de 30 minutos
        List<LocalTime> allSlots = new ArrayList<>();
        for (LocalTime time = start; !time.isAfter(end.minusMinutes(30)); time = time.plusMinutes(30)) {
            allSlots.add(time);
        }

        // 4. Obter os appointments existentes do médico nesse dia

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<Appointment> existingAppointments = springDataAppointmentRepository
                .findAppointmentsByPhysicianNumberAndDate(physicianNumber, startOfDay, endOfDay);

        Set<LocalTime> occupied = existingAppointments.stream()
                .map(a -> a.getDateTime().toLocalTime())
                .collect(Collectors.toSet());

        // 5. Retornar apenas os slots disponíveis
        return allSlots.stream()
                .filter(slot -> !occupied.contains(slot))
                .collect(Collectors.toList());
    }
    @Override
    public Appointment scheduleAppointmentByPatient(Long userId, String physicianNumber, LocalDateTime dateTime, ConsultationType type) {
        int i;
        for(i = patientPort; i <= patientPort + 999; i++) {
            try {
                ResponseEntity<Patient> response = restTemplate.getForEntity(serverUrl + i + url_patient + "id/" +userId + "/profile", Patient.class);
                if (response.getStatusCode() != HttpStatus.OK) {
                    throw new ResponseStatusException(response.getStatusCode(),
                            "Service at port " + i + " responded with status: " + response.getStatusCode());

                }



            } catch (HttpClientErrorException | HttpServerErrorException ex) {
                // For 4xx or 5xx HTTP errors that cause exceptions to be thrown
                throw new ResponseStatusException(ex.getStatusCode(),
                        "Service at port " + i + " returned error: " + ex.getStatusCode());
            } catch (ResourceAccessException ex) {
                // Service unreachable at this port — optionally continue trying next port
                // Or throw if you want to fail fast
            }
        }



        String patientUrl = serverUrl + i + url_patient + "id/" +userId + "/profile";
        Patient patient = restTemplate.getForObject(patientUrl, Patient.class);

        // assert patient != null;
        String patientNumber = patient.getPatientNumber();

        patientUrl = serverUrl + i + url_patient + "/number/" + patientNumber;
        patient = restTemplate.getForObject(patientUrl, Patient.class);

        for(i = physicianPort; i <= physicianPort + 999; i++) {
            try {
                ResponseEntity<Patient> response = restTemplate.getForEntity(serverUrl + i + url_physician, Patient.class);
                if (response.getStatusCode() != HttpStatus.OK) {
                    throw new ResponseStatusException(response.getStatusCode(),
                            "Service at port " + i + " responded with status: " + response.getStatusCode());

                }



            } catch (HttpClientErrorException | HttpServerErrorException ex) {
                // For 4xx or 5xx HTTP errors that cause exceptions to be thrown
                throw new ResponseStatusException(ex.getStatusCode(),
                        "Service at port " + i + " returned error: " + ex.getStatusCode());
            } catch (ResourceAccessException ex) {
                // Service unreachable at this port — optionally continue trying next port
                // Or throw if you want to fail fast
            }
        }

        String physicianUrl = serverUrl + i + url_physician + "/number/" + physicianNumber;
        Physician physician = restTemplate.getForObject(physicianUrl, Physician.class);

        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A consulta não pode ser no passado.");
        }

        List<LocalTime> availableSlots = getAvailableSlots(physicianNumber, dateTime.toLocalDate());

        if (!availableSlots.contains(dateTime.toLocalTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data selecionada não está disponível para o médico.");
        }

        // GERAR O PRÓXIMO appointmentNumber
        Integer max = appointmentRepository.findMaxAppointmentNumber();
        int next = (max == null) ? 1 : max + 1;
        AppointmentNumber generatedNumber = new AppointmentNumber("APPT-" + next);

        Appointment appointment = new Appointment(dateTime, type, AppointmentStatus.SCHEDULED, patient, physician);
        appointment.setAppointmentNumber(generatedNumber);

        return appointmentRepository.save(appointment);
    }

    @Override
    public List<AgeGroupStats> getAppointmentStatsByAgeGroup() {
        List<Appointment> appointments = appointmentRepository.findAll();

        LocalDate now = LocalDate.now();

        Map<String, List<Appointment>> groupedByAge = appointments.stream()
                .filter(appt -> appt.getStartTime() != null && appt.getEndTime() != null)
                .collect(Collectors.groupingBy(appt -> {
                    LocalDate birthDate = LocalDate.parse(appt.getPatient().getDateOfBirth());
                    int age = Period.between(birthDate, now).getYears();

                    if (age < 18) return "0-17";
                    else if (age < 30) return "18-29";
                    else if (age < 45) return "30-44";
                    else if (age < 60) return "45-59";
                    else return "60+";
                }));

        List<AgeGroupStats> stats = new ArrayList<>();
        for (Map.Entry<String, List<Appointment>> entry : groupedByAge.entrySet()) {
            String ageGroup = entry.getKey();
            List<Appointment> groupAppointments = entry.getValue();

            double averageDuration = groupAppointments.stream()
                    .mapToLong(appt -> Duration.between(appt.getStartTime(), appt.getEndTime()).toMinutes())
                    .average()
                    .orElse(0.0);

            stats.add(new AgeGroupStats(ageGroup, groupAppointments.size(), averageDuration));
        }

        return stats;
    }

    @Override
    public List<Appointment> getAppointmentHistory(Long userId) {
        int i =  patientPort;
        for(i = patientPort; i <= patientPort + 999; i++) {
            try {
                ResponseEntity<Patient> response = restTemplate.getForEntity(
                        "http://localhost:" + i + "/api/patients/id/" + userId + "/profile", Patient.class);

                if (response.getStatusCode() == HttpStatus.OK || response.getBody() != null) {
                    break;
                }
            } catch (ResourceAccessException e) {
                // This is likely due to connection issues, e.g., wrong port
                System.out.println("Cannot connect to port " + i + ": " + e.getMessage());
                break;  // or handle accordingly
            }
        }

        String patientUrl = "http://localhost:" + i + "/api/patients/id/" + userId + "/profile";
        Patient patient = restTemplate.getForObject(patientUrl, Patient.class);
        String patientNumber = patient.getPatientNumber();
        return appointmentRepository.findByPatient_PatientNumberOrderByDateTimeDesc(patientNumber);
    }

    @Override
    public List<Appointment> getUpcomingAppointments() {
        LocalDateTime now = LocalDateTime.now();
        return appointmentRepository.findUpcomingAppointments(now);
    }

    @Override
    public List<PhysicianAverageDurationDTO> getAverageAppointmentDurationPerPhysician() {
        List<Object[]> results = springDataAppointmentRepository.findAverageAppointmentDurationPerPhysician();
        List<PhysicianAverageDurationDTO> dtos = new ArrayList<>();

        for (Object[] row : results) {
            String name = (String) row[0];
            Double avgSeconds = row[1] != null ? ((Number) row[1]).doubleValue() : 0;
            dtos.add(new PhysicianAverageDurationDTO(name, avgSeconds));
        }

        return dtos;
    }

    @Data
    @AllArgsConstructor
    public class MonthlyAppointmentReport {
        private String month;
        private long totalAppointments;
        private long cancelledAppointments;
        private long rescheduledAppointments;
    }

    @Override
    public List<MonthlyAppointmentReport> getMonthlyReport() {
        return appointmentRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        a -> a.getDateTime().getMonth(), // Agrupar por mês
                        Collectors.toList()
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    List<Appointment> appointments = entry.getValue();

                    long total = appointments.size();
                    long cancelled = appointments.stream()
                            .filter(a -> a.getStatus() == AppointmentStatus.CANCELLED)
                            .count();
                    long rescheduled = appointments.stream()
                            .filter(a -> a.getStatus() == AppointmentStatus.RESCHEDULED)
                            .count();

                    return new MonthlyAppointmentReport(
                            entry.getKey().name(), // Nome do mês em maiúsculas
                            total,
                            cancelled,
                            rescheduled
                    );
                })
                .collect(Collectors.toList());
    }


}
