package com.harshit.moviebooking.controller;

import com.harshit.moviebooking.dto.rating.RatingRequestDto;
import com.harshit.moviebooking.dto.rating.RatingResponseDto;
import com.harshit.moviebooking.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/{movieId}/ratings")
    public ResponseEntity<RatingResponseDto> rateMovie(
            @PathVariable Long movieId,
            @Valid @RequestBody RatingRequestDto requestDto) {

        RatingResponseDto response = ratingService.rateMovie(movieId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}