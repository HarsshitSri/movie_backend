package com.harshit.moviebooking.repository;

import com.harshit.moviebooking.entity.Movie;
import com.harshit.moviebooking.entity.User;
import com.harshit.moviebooking.entity.WatchlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<WatchlistEntry, Long> {

    boolean existsByUserAndMovie(User user, Movie movie);

    Optional<WatchlistEntry> findByUserAndMovie(User user, Movie movie);

    @Query("""
            SELECT w FROM WatchlistEntry w
            JOIN FETCH w.movie
            WHERE w.user = :user
            ORDER BY w.createdAt DESC
            """)
    List<WatchlistEntry> findByUserOrderByCreatedAtDesc(@Param("user") User user);
}
