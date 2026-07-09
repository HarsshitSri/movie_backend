package com.harshit.moviebooking.mapper;

import com.harshit.moviebooking.dto.rating.RatingResponseDto;
import com.harshit.moviebooking.entity.Rating;

public class RatingMapper {

    private RatingMapper() {
    }

    public static RatingResponseDto toResponseDto(Rating rating) {

        RatingResponseDto dto = new RatingResponseDto();

        dto.setId(rating.getId());
        dto.setUserId(rating.getUser().getId());
        dto.setMovieId(rating.getMovie().getId());
        dto.setRating(rating.getRating());
        dto.setCreatedAt(rating.getCreatedAt());
        dto.setUpdatedAt(rating.getUpdatedAt());

        return dto;
    }

}