package com.harshit.moviebooking.controller;

import com.harshit.moviebooking.dto.review.ReviewRequestDto;
import com.harshit.moviebooking.dto.review.ReviewResponseDto;
import com.harshit.moviebooking.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{movieId}/reviews")
    public ResponseEntity<List<ReviewResponseDto>> listByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(reviewService.listByMovie(movieId));
    }

    @PostMapping("/{movieId}/reviews")
    public ResponseEntity<ReviewResponseDto> upsert(
            @PathVariable Long movieId,
            @Valid @RequestBody ReviewRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.upsert(movieId, requestDto));
    }

    @DeleteMapping("/{movieId}/reviews/me")
    public ResponseEntity<Void> deleteMine(@PathVariable Long movieId) {
        reviewService.deleteMine(movieId);
        return ResponseEntity.noContent().build();
    }
}
