package com.harshit.moviebooking.service.impl;

import com.harshit.moviebooking.dto.review.ReviewRequestDto;
import com.harshit.moviebooking.dto.review.ReviewResponseDto;
import com.harshit.moviebooking.entity.Movie;
import com.harshit.moviebooking.entity.Review;
import com.harshit.moviebooking.entity.User;
import com.harshit.moviebooking.exception.MovieNotFoundException;
import com.harshit.moviebooking.mapper.ReviewMapper;
import com.harshit.moviebooking.repository.MovieRepo;
import com.harshit.moviebooking.repository.ReviewRepository;
import com.harshit.moviebooking.repository.UserRepo;
import com.harshit.moviebooking.security.CustomUserDetails;
import com.harshit.moviebooking.service.ReviewService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepo movieRepository;
    private final UserRepo userRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             MovieRepo movieRepository,
                             UserRepo userRepository) {
        this.reviewRepository = reviewRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ReviewResponseDto upsert(Long movieId, ReviewRequestDto requestDto) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));
        User user = currentUser();

        Optional<Review> existing = reviewRepository.findByUserAndMovie(user, movie);
        Review review = existing.orElse(new Review());
        review.setUser(user);
        review.setMovie(movie);
        review.setBody(requestDto.getBody().trim());

        LocalDateTime now = LocalDateTime.now();
        if (review.getId() == null) {
            review.setCreatedAt(now);
        }
        review.setUpdatedAt(now);

        return ReviewMapper.toResponseDto(reviewRepository.save(review));
    }

    @Override
    public void deleteMine(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));
        User user = currentUser();
        reviewRepository.findByUserAndMovie(user, movie)
                .ifPresent(reviewRepository::delete);
    }

    @Override
    public List<ReviewResponseDto> listByMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));
        return reviewRepository.findByMovieOrderByUpdatedAtDesc(movie).stream()
                .map(ReviewMapper::toResponseDto)
                .toList();
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
