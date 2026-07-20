package com.harshit.moviebooking.config;

import com.harshit.moviebooking.entity.Role;
import com.harshit.moviebooking.enums.RoleName;
import com.harshit.moviebooking.repository.RoleRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RoleDataInitializer.class);

    private final RoleRepo roleRepo;

    public RoleDataInitializer(RoleRepo roleRepo) {
        this.roleRepo = roleRepo;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedRole(RoleName.USER, "Default registered user");
        seedRole(RoleName.ADMIN, "Administrator");
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
}
