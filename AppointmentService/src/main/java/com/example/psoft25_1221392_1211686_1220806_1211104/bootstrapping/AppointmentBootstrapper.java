package com.example.psoft25_1221392_1211686_1220806_1211104.bootstrapping;

import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.Appointment;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.AppointmentNumber;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.AppointmentStatus;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.ConsultationType;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.repositories.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.web.client.RestTemplate;

/**
 * Lê src/main/resources/Patient.txt (CSV delimitado por ;) e popula a tabela PATIENT.
 * Se o campo patientNumber vier vazio, gera-o no formato AAAA/N onde N é incremental.
 */
@Component
@RequiredArgsConstructor
@Profile("bootstrap")
@Order(6)      // corre depois do PhysicianBootstrapper (que tem @Order(5))
public class AppointmentBootstrapper implements CommandLineRunner {

    private final AppointmentRepository appointmentRepo;

    private LocalDateTime parseDateOrNull(String text) {
        if (text == null || text.isBlank() || text.equalsIgnoreCase("null")) {
            return null;
        }
        return LocalDateTime.parse(text);
    }

    @Autowired
    @Qualifier("restTemplateWithAuth")
    private RestTemplate restTemplate;


    private final String PHYSICIAN_SERVICE_URL = "http://localhost:8082/api/physicians";

    private final String PATIENT_SERVICE_URL = "http://localhost:8099/api/patients";

    @Override
    public void run(String... args) throws Exception {

        try (BufferedReader reader =
                     Files.newBufferedReader(Paths.get("src/main/resources/Appointment.txt"))) {
            int counter = 1;

            String line;
            boolean first = true;

            while ((line = reader.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }

                String[] f = line.split(";", -1); // <- -1 mantém colunas vazias
                if (f.length < 8) {
                    System.err.println("Linha inválida: " + line);
                    continue;
                }

                String patientNumber = f[0].trim();
                String physicianNumber = f[1].trim();
                LocalDateTime dt = LocalDateTime.parse(f[2].trim());
                ConsultationType ct = ConsultationType.valueOf(f[3].trim());
                AppointmentStatus st = AppointmentStatus.valueOf(f[4].trim());
                LocalDateTime start = parseDateOrNull(f[5].trim()); // <- protegido
                LocalDateTime end = parseDateOrNull(f[6].trim());   // <- protegido

                Optional<Appointment> existing = appointmentRepo.findByPatientIdAndPhysicianNumberAndDateTime(patientNumber, physicianNumber, dt);
                Appointment a = existing.orElseGet(() -> new Appointment(dt, ct, st, patientNumber, physicianNumber));
                a.setStartTime(start);
                a.setEndTime(end);
                a.setAppointmentNumber(new AppointmentNumber("APPT-" + counter++));
                appointmentRepo.save(a);
            }
        }
    }
}
