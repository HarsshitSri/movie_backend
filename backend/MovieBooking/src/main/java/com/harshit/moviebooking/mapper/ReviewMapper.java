package com.harshit.moviebooking.mapper;

import com.harshit.moviebooking.dto.review.ReviewResponseDto;
import com.harshit.moviebooking.entity.Review;

public class ReviewMapper {

    private ReviewMapper() {
    }

    public static ReviewResponseDto toResponseDto(Review review) {
        ReviewResponseDto dto = new ReviewResponseDto();
        dto.setId(review.getId());
        dto.setUserId(review.getUser().getId());
        dto.setUsername(review.getUser().getUsername());
        dto.setMovieId(review.getMovie().getId());
        dto.setBody(review.getBody());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        return dto;
    }
}
