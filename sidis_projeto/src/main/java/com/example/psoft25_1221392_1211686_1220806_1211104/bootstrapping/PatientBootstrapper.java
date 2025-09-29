package com.example.psoft25_1221392_1211686_1220806_1211104.bootstrapping;

import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.Role;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Patient;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.PatientNumber;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.repositories.PatientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Patient.newPatient;

/**
 * Lê src/main/resources/Patient.txt (CSV delimitado por ;) e popula a tabela PATIENT.
 * Se o campo patientNumber vier vazio, gera-o no formato AAAA/N onde N é incremental.
 */
@Component
@RequiredArgsConstructor
@Profile("bootstrap")
@Order(4)
public class PatientBootstrapper implements CommandLineRunner {
    private final PasswordEncoder passwordEncoder;

    private final PatientRepository patientRepo;

    @Override
    public void run(final String... args) {
        try (BufferedReader reader =
                     Files.newBufferedReader(Paths.get("src/main/resources/Patient.txt"))) {

            String line;
            boolean isFirstLine     = true;

            // utilitário para gerar números de paciente
            PatientNumber generator = new PatientNumber();
            long nextSequential     = generator.getNextPatientNumber(patientRepo); // primeiro livre

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {           // salta o cabeçalho
                    isFirstLine = false;
                    continue;
                }

                String[] fields = line.split(";", -1);  // -1 mantém campos vazios
                if (fields.length < 8) {
                    System.err.println("Linha inválida (esperava 8 colunas): " + line);
                    continue;
                }

                /* --- campos do CSV --- */
                String patientNumberInFile  = fields[0].trim();  // pode vir vazio
                String emailAddress         = fields[1].trim();
                String name                 = fields[2].trim();
                String dateOfBirth          = fields[3].trim();
                String phoneNumber          = fields[4].trim();
                String address              = fields[5].trim();
                String insuranceInformation = fields[6].trim();
                String dataConsent          = fields[7].trim();

                /* --- gera patientNumber se necessário --- */
                String patientNumber = patientNumberInFile;
                if (patientNumber.isEmpty()) {
                    patientNumber = generator.generate(nextSequential++);
                }

                createPatientIfNotExists(
                        patientNumber,
                        emailAddress,
                        name,
                        dateOfBirth,
                        phoneNumber,
                        address,
                        insuranceInformation,
                        dataConsent
                );
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Cria e grava o paciente somente se ainda não existir esse patientNumber. */
    @Transactional
    private void createPatientIfNotExists(
            final String patientNumber,
            final String emailAddress,
            final String name,
            final String dateOfBirth,
            final String phoneNumber,
            final String address,
            final String insuranceInformation,
            final String dataConsent) {

        if (patientRepo.findByPatientNumber(patientNumber).isEmpty()) {


            User user = User.newUser(
                    emailAddress,
                    passwordEncoder.encode("password"), // define uma password default
                    name,
                    Role.PATIENT
            );

            Patient patient = newPatient(
                    emailAddress,
                    name,
                    dateOfBirth,
                    phoneNumber,
                    address,
                    insuranceInformation,
                    dataConsent,
                    user
            );

            patient.setPatientNumber(patientNumber);
            patientRepo.save(patient);
        }
    }

}

