package com.harshit.moviebooking.dto.watchlist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WatchlistItemDto {

    private Long id;
    private Long movieId;
    private String title;
    private BigDecimal averageRating;
    private Integer ratingCount;
    private LocalDateTime addedAt;
}
