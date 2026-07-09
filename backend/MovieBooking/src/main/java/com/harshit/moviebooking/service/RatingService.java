package com.harshit.moviebooking.service;

import com.harshit.moviebooking.dto.rating.RatingRequestDto;
import com.harshit.moviebooking.dto.rating.RatingResponseDto;

public interface RatingService {

    RatingResponseDto rateMovie(Long movieId,
                                RatingRequestDto requestDto);

}