package com.harshit.moviebooking.mapper;

import com.harshit.moviebooking.dto.movie.MovieRequestDto;
import com.harshit.moviebooking.dto.movie.MovieResponseDto;
import com.harshit.moviebooking.entity.Movie;

public final class MovieMapper {

    private MovieMapper() {
    }

    public static Movie toEntity(MovieRequestDto dto) {

        Movie movie = new Movie();

        movie.setTitle(dto.getTitle());
        movie.setSynopsis(dto.getSynopsis());
        movie.setReleaseDate(dto.getReleaseDate());
        movie.setRuntimeMinutes(dto.getRuntimeMinutes());
        movie.setLanguage(dto.getLanguage());
        movie.setCountryOfOrigin(dto.getCountryOfOrigin());
        movie.setContentRating(dto.getContentRating());
        movie.setPosterUrl(dto.getPosterUrl());

        return movie;
    }

    public static MovieResponseDto toResponseDto(Movie movie) {

        MovieResponseDto dto = new MovieResponseDto();

        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setSynopsis(movie.getSynopsis());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setRuntimeMinutes(movie.getRuntimeMinutes());
        dto.setLanguage(movie.getLanguage());
        dto.setCountryOfOrigin(movie.getCountryOfOrigin());
        dto.setContentRating(movie.getContentRating());
        dto.setPosterUrl(movie.getPosterUrl());
        dto.setAverageRating(movie.getAverageRating());
        dto.setRatingCount(movie.getRatingCount());

        return dto;
    }
}