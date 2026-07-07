package com.harshit.moviebooking.service;

import com.harshit.moviebooking.dto.movie.MovieRequestDto;
import com.harshit.moviebooking.dto.movie.MovieResponseDto;

public interface MovieService {

    MovieResponseDto createMovie(MovieRequestDto requestDto);

}