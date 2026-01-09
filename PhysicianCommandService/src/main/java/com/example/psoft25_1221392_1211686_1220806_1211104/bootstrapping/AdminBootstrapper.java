package com.example.psoft25_1221392_1211686_1220806_1211104.bootstrapping;

import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.Role;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.User;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("bootstrap")
@Order(1)
public class AdminBootstrapper implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String email = "admin@example.com";

        if (userRepository.findByUsername(email).isEmpty()) {
            User admin = new User();
            admin.setUsername(email);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Admin User");
            admin.addAuthority(new Role(Role.ADMIN));

            userRepository.save(admin);
            System.out.println("Admin user criado com sucesso.");
        } else {
            System.out.println("Admin user já existe.");
        }
    }
}
