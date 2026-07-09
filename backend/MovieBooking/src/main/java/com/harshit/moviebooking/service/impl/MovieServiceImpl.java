package com.harshit.moviebooking.service.impl;

import com.harshit.moviebooking.dto.movie.MovieRequestDto;
import com.harshit.moviebooking.dto.movie.MovieResponseDto;
import com.harshit.moviebooking.entity.Movie;
import com.harshit.moviebooking.exception.MovieNotFoundException;
import com.harshit.moviebooking.mapper.MovieMapper;
import com.harshit.moviebooking.repository.MovieRepo;
import com.harshit.moviebooking.service.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    public MovieResponseDto getMovieById(Long id) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));

        return MovieMapper.toResponseDto(movie);
    }

    @Override
    public Page<MovieResponseDto> getAllMovies(Pageable pageable) {

        return movieRepository.findAll(pageable)
                .map(MovieMapper::toResponseDto);
    }

    @Override
    public MovieResponseDto updateMovie(Long id, MovieRequestDto requestDto) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));

        movie.setTitle(requestDto.getTitle());
        movie.setSynopsis(requestDto.getSynopsis());
        movie.setReleaseDate(requestDto.getReleaseDate());
        movie.setRuntimeMinutes(requestDto.getRuntimeMinutes());
        movie.setLanguage(requestDto.getLanguage());
        movie.setCountryOfOrigin(requestDto.getCountryOfOrigin());
        movie.setContentRating(requestDto.getContentRating());
        movie.setPosterUrl(requestDto.getPosterUrl());

        movie.setUpdatedAt(LocalDateTime.now());

        Movie updatedMovie = movieRepository.save(movie);

        return MovieMapper.toResponseDto(updatedMovie);
    }

    @Override
    public void deleteMovie(Long id) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));

        movieRepository.delete(movie);
    }

    @Override
    public List<MovieResponseDto> searchMoviesByTitle(String title) {

        return movieRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(MovieMapper::toResponseDto)
                .toList();
    }
}

