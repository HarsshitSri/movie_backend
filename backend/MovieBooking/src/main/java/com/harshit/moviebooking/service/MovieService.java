package com.harshit.moviebooking.service;

import com.harshit.moviebooking.dto.movie.MovieRequestDto;
import com.harshit.moviebooking.dto.movie.MovieResponseDto;

import java.util.List;

public interface MovieService {

    MovieResponseDto createMovie(MovieRequestDto requestDto);

    MovieResponseDto getMovieById(Long id);

    List<MovieResponseDto> getAllMovies();

    MovieResponseDto updateMovie(Long id, MovieRequestDto requestDto);

    void deleteMovie(Long id);
}
