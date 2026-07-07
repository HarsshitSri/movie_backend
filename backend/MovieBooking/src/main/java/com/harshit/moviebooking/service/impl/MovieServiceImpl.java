package com.harshit.moviebooking.service.impl;

import com.harshit.moviebooking.dto.movie.MovieRequestDto;
import com.harshit.moviebooking.dto.movie.MovieResponseDto;
import com.harshit.moviebooking.entity.Movie;
import com.harshit.moviebooking.mapper.MovieMapper;
import com.harshit.moviebooking.repository.MovieRepo;
import com.harshit.moviebooking.service.MovieService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepo movieRepository;

    public MovieServiceImpl(MovieRepo movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public MovieResponseDto createMovie(MovieRequestDto requestDto) {

        Movie movie = MovieMapper.toEntity(requestDto);

        movie.setCreatedAt(LocalDateTime.now());
        movie.setUpdatedAt(LocalDateTime.now());

        movie.setAverageRating(BigDecimal.ZERO);
        movie.setRatingCount(0);

        Movie savedMovie = movieRepository.save(movie);

        return MovieMapper.toResponseDto(savedMovie);
    }
}