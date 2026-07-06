package com.harshit.moviebooking.service.impl;

import com.harshit.moviebooking.repository.MovieRepo;
import com.harshit.moviebooking.service.MovieService;
import org.springframework.stereotype.Service;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepo movieRepository;

    public MovieServiceImpl(MovieRepo movieRepository) {
        this.movieRepository = movieRepository;
    }
}
