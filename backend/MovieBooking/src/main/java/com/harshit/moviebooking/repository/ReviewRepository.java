package com.harshit.moviebooking.repository;

import com.harshit.moviebooking.entity.Movie;
import com.harshit.moviebooking.entity.Review;
import com.harshit.moviebooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByUserAndMovie(User user, Movie movie);

    @Query("""
            SELECT r FROM Review r
            JOIN FETCH r.user
            WHERE r.movie = :movie
            ORDER BY r.updatedAt DESC
            """)
    List<Review> findByMovieOrderByUpdatedAtDesc(@Param("movie") Movie movie);
}
