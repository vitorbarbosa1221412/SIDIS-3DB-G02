package com.example.psoft25_1221392_1211686_1220806_1211104.bootstrapping;

//import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.Physician;
//import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.PhysicianNumber;
//import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.repositories.PhysicianRepository;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.Role;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
@Profile("bootstrap")
@Order(5)
public class PhysicianBootstrapper implements CommandLineRunner {

//    private final PhysicianRepository physicianRepo;
    private final PasswordEncoder passwordEncoder; // 🔐 Para codificar a password
//    private final PhysicianNumber numberGenerator = new PhysicianNumber();

    @Override
    public void run(String... args) {
        try (BufferedReader reader =
                     Files.newBufferedReader(Paths.get("src/main/resources/Physician.txt"))) {

            boolean first = true;
            //long nextSeq = numberGenerator.getNextPhysicianNumber(physicianRepo);

            String line;
            while ((line = reader.readLine()) != null) {
                if (first) { first = false; continue; }

                String[] f = line.split(";", -1);
                if (f.length < 8) {
                    System.err.println("Linha inválida: " + line);
                    continue;
                }

//                String physicianNumber =
//                        f[0].trim().isBlank()
//                                ? numberGenerator.generate(nextSeq++)
//                                : f[0].trim();

//                createPhysicianIfNotExists(
//                        physicianNumber,
//                        f[1].trim(), // name
//                        f[2].trim(), // address
//                        f[3].trim(), // phone
//                        f[4].trim(), // email
//                        f[5].trim(), // department
//                        f[6].trim(), // specialty
//                        f[7].trim()  // working hours
//                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createPhysicianIfNotExists(String physicianNumber, String name,
                                            String address, String phone, String email,
                                            String dept, String specialty, String hours) {

//        if (physicianRepo.findByPhysicianNumber(physicianNumber).isEmpty()) {
//            String encodedPassword = passwordEncoder.encode("password"); // 🔐 password codificada
//            User user = User.newUser(email, encodedPassword, name, Role.PHYSICIAN);
//
//            Physician physician = Physician.newPhysician(
//                    null, name, address, phone, email, dept, specialty, hours, user, null
//            );
//
//            physician.setPhysicianNumber(physicianNumber);
//            physicianRepo.save(physician);
//        }
    }
}
