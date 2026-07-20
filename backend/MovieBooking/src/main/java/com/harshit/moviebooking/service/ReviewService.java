package com.harshit.moviebooking.service;

import com.harshit.moviebooking.dto.review.ReviewRequestDto;
import com.harshit.moviebooking.dto.review.ReviewResponseDto;

import java.util.List;

public interface ReviewService {

    ReviewResponseDto upsert(Long movieId, ReviewRequestDto requestDto);

    void deleteMine(Long movieId);

    List<ReviewResponseDto> listByMovie(Long movieId);
}
