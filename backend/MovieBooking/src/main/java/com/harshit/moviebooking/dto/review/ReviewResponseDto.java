package com.harshit.moviebooking.dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {

    private Long id;
    private Long userId;
    private String username;
    private Long movieId;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
