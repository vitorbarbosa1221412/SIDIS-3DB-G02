    package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model;

    import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.Role;
    import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.User;
    import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.ProfilePicture;
    import jakarta.persistence.*;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.NotNull;
    import jakarta.validation.constraints.Size;
    import lombok.Getter;
    import lombok.Setter;


    @Entity
    @Getter
    @Setter
    public class Physician {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        @Column(nullable = false, updatable = false)
        @NotNull(message = "O nome do médico não pode ser nulo.")
        @NotBlank
        @Size(min = 1, max = 255)
        private String name;

        @Column(nullable = false, updatable = false)
        @NotNull(message = "O endereço não pode ser nulo.")
        @NotBlank
        @Size(min = 1, max = 255)
        private String address;

        @Column(nullable = false, unique = true, updatable = false)
        @NotNull
        @NotBlank
        @Setter
        @Getter
        @Size(min = 1, max = 32)
        private String physicianNumber;

        @OneToOne(cascade = CascadeType.ALL)
        @JoinColumn(name = "userId")
        private User user;

        @Column(nullable = false, unique = true, updatable = false)
        @NotNull(message = "O número de telefone não pode ser nulo.")
        @NotBlank
        @Size(min = 4, max = 17)
        private String phoneNumber;

        @Column(nullable = false, unique = true, updatable = false)
        @NotNull(message = "O email não pode ser nulo.")
        @NotBlank
        @Size(min = 1, max = 255)
        private String emailAddress;

        @Setter
        @Getter
        private boolean enabled = true;

        @Column(nullable = false, updatable = false)
        @NotNull(message = "O departamento não pode ser nulo.")
        @NotBlank
        @Size(min = 1, max = 255)
        private String department;

        @Column(nullable = false, updatable = false)
        @NotNull(message = "A especialidade não pode ser nula.")
        @NotBlank
        @Size(min = 1, max = 255)
        private String specialty;

        @Column(nullable = false, updatable = false)
        @NotNull(message = "O horário não pode ser nulo.")
        @NotBlank
        private String workingHours;

        @Embedded
        private ProfilePicture profilePicture;


        public Physician() {}

        public Physician(Long id, String name, String address, String phoneNumber, String emailAddress,
                         String department, String specialty, String workingHours, ProfilePicture profilePicture) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.phoneNumber = phoneNumber;
            this.emailAddress = emailAddress;
            this.department = department;
            this.specialty = specialty;
            this.workingHours = workingHours;
            this.profilePicture = profilePicture;
        }

        public static Physician newPhysician(Long id, String name, String address, String phoneNumber,
                                             String emailAddress, String department, String specialty,
                                             String workingHours, final User user, ProfilePicture profilePicture) {
            final var p = new Physician(id, name, address, phoneNumber, emailAddress, department, specialty, workingHours, profilePicture);
            p.setUser(user);
            user.setFullName(name);
            user.addAuthority(new Role(Role.PHYSICIAN));
            return p;
        }

        @Override
        public String toString() {
            return "Physician [id=" + id +
                    ", name=" + name +
                    ", address=" + address +
                    ", phoneNumber=" + phoneNumber +
                    ", emailAddress=" + emailAddress +
                    ", specialty=" + specialty +
                    ", workingHours=" + workingHours +
                    ", profilePicture=" + (profilePicture != null ? profilePicture.getFileName() : "none") +
                    "]";
        }


    }

