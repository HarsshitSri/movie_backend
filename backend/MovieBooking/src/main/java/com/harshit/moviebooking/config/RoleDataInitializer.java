package com.harshit.moviebooking.config;

import com.harshit.moviebooking.entity.Role;
import com.harshit.moviebooking.entity.User;
import com.harshit.moviebooking.enums.AccountStatus;
import com.harshit.moviebooking.enums.RoleName;
import com.harshit.moviebooking.repository.RoleRepo;
import com.harshit.moviebooking.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class RoleDataInitializer implements ApplicationRunner {

    public static final String ADMIN_EMAIL = "admin@movieplatform.local";
    public static final String ADMIN_PASSWORD = "Admin@12345";

    private static final Logger log = LoggerFactory.getLogger(RoleDataInitializer.class);

    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public RoleDataInitializer(RoleRepo roleRepo,
                               UserRepo userRepo,
                               PasswordEncoder passwordEncoder) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedRole(RoleName.USER, "Default registered user");
        seedRole(RoleName.ADMIN, "Administrator");
        seedAdminUser();
    }

    private void seedRole(RoleName name, String description) {
        if (roleRepo.findByName(name).isEmpty()) {
            Role role = new Role();
            role.setName(name);
            role.setDescription(description);
            roleRepo.save(role);
            log.info("Seeded role: {}", name);
        }
    }

    private void seedAdminUser() {
        Role adminRole = roleRepo.findByName(RoleName.ADMIN)
                .orElseThrow(() -> new IllegalStateException("ADMIN role missing"));

        userRepo.findByEmail(ADMIN_EMAIL).ifPresentOrElse(admin -> {
            admin.setPasswordHash(passwordEncoder.encode(ADMIN_PASSWORD));
            admin.setRole(adminRole);
            admin.setAccountStatus(AccountStatus.ACTIVE);
            admin.setUpdatedAt(LocalDateTime.now());
            userRepo.save(admin);
            log.info("Reset admin password for {}", ADMIN_EMAIL);
        }, () -> {
            User admin = new User();
            admin.setUsername("admin");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail(ADMIN_EMAIL);
            admin.setPasswordHash(passwordEncoder.encode(ADMIN_PASSWORD));
            admin.setRole(adminRole);
            admin.setDateOfBirth(LocalDate.of(1990, 1, 1));
            admin.setAccountStatus(AccountStatus.ACTIVE);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            userRepo.save(admin);
            log.info("Seeded admin user {} (password: {})", ADMIN_EMAIL, ADMIN_PASSWORD);
        });
    }
}
