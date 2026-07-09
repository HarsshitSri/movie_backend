package com.harshit.moviebooking.repository;

import com.harshit.moviebooking.entity.Movie;
import com.harshit.moviebooking.entity.Rating;
import com.harshit.moviebooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    boolean existsByUserAndMovie(User user, Movie movie);

    Optional<Rating> findByUserAndMovie(User user, Movie movie);

    List<Rating> findByMovie(Movie movie);

}