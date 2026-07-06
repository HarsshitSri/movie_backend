package com.harshit.moviebooking.repository;

import com.harshit.moviebooking.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Long> {
}
