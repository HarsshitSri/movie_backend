package com.harshit.moviebooking.service.impl;

import com.harshit.moviebooking.dto.rating.RatingRequestDto;
import com.harshit.moviebooking.dto.rating.RatingResponseDto;
import com.harshit.moviebooking.entity.Movie;
import com.harshit.moviebooking.entity.Rating;
import com.harshit.moviebooking.entity.User;
import com.harshit.moviebooking.exception.MovieNotFoundException;
import com.harshit.moviebooking.mapper.RatingMapper;
import com.harshit.moviebooking.repository.MovieRepo;
import com.harshit.moviebooking.repository.RatingRepository;
import com.harshit.moviebooking.repository.UserRepo;
import com.harshit.moviebooking.security.CustomUserDetails;
import com.harshit.moviebooking.service.RatingService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final MovieRepo movieRepository;
    private final UserRepo userRepository;

    public RatingServiceImpl(RatingRepository ratingRepository,
                             MovieRepo movieRepository,
                             UserRepo userRepository) {

        this.ratingRepository = ratingRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }

    @Override
    public RatingResponseDto rateMovie(Long movieId,
                                       RatingRequestDto requestDto) {

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));

        User user = currentUser();

        Optional<Rating> existingRating =
                ratingRepository.findByUserAndMovie(user, movie);

        Rating rating = existingRating.orElse(new Rating());

        rating.setMovie(movie);
        rating.setUser(user);
        rating.setRating(requestDto.getRating());

        if (rating.getId() == null) {
            rating.setCreatedAt(LocalDateTime.now());
        }

        rating.setUpdatedAt(LocalDateTime.now());

        Rating savedRating = ratingRepository.save(rating);

        List<Rating> ratings = ratingRepository.findByMovie(movie);

        double average = ratings.stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0.0);

        movie.setAverageRating(
                BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP));
        movie.setRatingCount(ratings.size());

        movieRepository.save(movie);

        return RatingMapper.toResponseDto(savedRating);
    }

    private User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Please log in first.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails details) {
            return details.getUser();
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Please log in first."));
    }
}
