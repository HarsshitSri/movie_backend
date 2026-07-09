package com.harshit.moviebooking.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponseDto {

    private Long id;

    private Long userId;

    private Long movieId;

    private Integer rating;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}