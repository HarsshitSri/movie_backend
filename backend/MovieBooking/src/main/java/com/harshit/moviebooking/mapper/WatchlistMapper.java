package com.harshit.moviebooking.mapper;

import com.harshit.moviebooking.dto.watchlist.WatchlistItemDto;
import com.harshit.moviebooking.entity.Movie;
import com.harshit.moviebooking.entity.WatchlistEntry;

public class WatchlistMapper {

    private WatchlistMapper() {
    }

    public static WatchlistItemDto toItemDto(WatchlistEntry entry) {
        Movie movie = entry.getMovie();
        WatchlistItemDto dto = new WatchlistItemDto();
        dto.setId(entry.getId());
        dto.setMovieId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setAverageRating(movie.getAverageRating());
        dto.setRatingCount(movie.getRatingCount());
        dto.setAddedAt(entry.getCreatedAt());
        return dto;
    }
}
