package com.harshit.moviebooking.service;

import com.harshit.moviebooking.dto.movie.MovieRequestDto;
import com.harshit.moviebooking.dto.movie.MovieResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MovieService {

    MovieResponseDto createMovie(MovieRequestDto requestDto);

    MovieResponseDto getMovieById(Long id);

    Page<MovieResponseDto> getAllMovies(Pageable pageable);

    MovieResponseDto updateMovie(Long id, MovieRequestDto requestDto);

    void deleteMovie(Long id);

    List<MovieResponseDto> searchMoviesByTitle(String title);
}
