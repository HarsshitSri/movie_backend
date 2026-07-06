package com.harshit.moviebooking.repository;

import com.harshit.moviebooking.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepo extends JpaRepository<Movie, Long> {

    }

