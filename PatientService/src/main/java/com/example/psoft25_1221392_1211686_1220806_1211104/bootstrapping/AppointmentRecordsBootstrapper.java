package com.example.psoft25_1221392_1211686_1220806_1211104.bootstrapping;

//import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.Appointment;
//import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.AppointmentNumber;
//import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.repositories.AppointmentRepository;
//import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.model.AppointmentRecord;
//import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.repositories.AppointmentRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
@Profile("bootstrap")
@Order(7)  // corre depois do AppointmentBootstrapper
public class AppointmentRecordsBootstrapper implements CommandLineRunner {


//    private final AppointmentRecordRepository appointmentRecordRepository;
//    private final AppointmentRepository appointmentRepository;

    @Override
    public void run(String... args) {
        try (BufferedReader reader =
                     Files.newBufferedReader(Paths.get("src/main/resources/AppointmentRecords.txt"))) {

            boolean first = true;
            String line;

            while ((line = reader.readLine()) != null) {
                if (first) { first = false; continue; }

                String[] f = line.split(";", -1);
                if (f.length < 5) {
                    System.err.println("Linha inválida: " + line);
                    continue;
                }

                Long recordNumber = Long.parseLong(f[0].trim());
                String diagnosis = f[1].trim();
                String treatmentRecommendation = f[2].trim();
                String prescription = f[3].trim();
                String appointmentNumber = f[4].trim();

//                Appointment appointment = appointmentRepository
//                        .findByAppointmentNumber(new AppointmentNumber(appointmentNumber))
//
//                        .orElseThrow(() -> new RuntimeException("Appointment not found: " + appointmentNumber));
//
//                // evita duplicados
//                if (appointmentRecordRepository.findByAppointment(appointment).isPresent()) continue;
//
//                AppointmentRecord record = new AppointmentRecord(
//                        diagnosis,
//                        treatmentRecommendation,
//                        prescription,
//                        appointment,
//                        recordNumber
//                );
//
//                appointmentRecordRepository.save(record);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
