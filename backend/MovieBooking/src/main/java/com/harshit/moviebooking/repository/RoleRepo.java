package com.harshit.moviebooking.repository;

import com.harshit.moviebooking.entity.Role;
import com.harshit.moviebooking.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);

}