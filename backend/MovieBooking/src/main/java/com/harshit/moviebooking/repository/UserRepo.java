package com.harshit.moviebooking.repository;

import com.harshit.moviebooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
}
