package com.harshit.moviebooking.service;

import com.harshit.moviebooking.dto.movie.MovieRequestDto;
import com.harshit.moviebooking.dto.movie.MovieResponseDto;
import com.harshit.moviebooking.entity.Movie;
import com.harshit.moviebooking.mapper.MovieMapper;
import com.harshit.moviebooking.repository.MovieRepo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface MovieService {

    MovieResponseDto createMovie(MovieRequestDto requestDto);

    }

}

