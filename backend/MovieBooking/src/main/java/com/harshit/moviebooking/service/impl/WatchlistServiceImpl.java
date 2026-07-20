package com.harshit.moviebooking.service.impl;

import com.harshit.moviebooking.dto.watchlist.WatchlistItemDto;
import com.harshit.moviebooking.entity.Movie;
import com.harshit.moviebooking.entity.User;
import com.harshit.moviebooking.entity.WatchlistEntry;
import com.harshit.moviebooking.exception.MovieNotFoundException;
import com.harshit.moviebooking.mapper.WatchlistMapper;
import com.harshit.moviebooking.repository.MovieRepo;
import com.harshit.moviebooking.repository.UserRepo;
import com.harshit.moviebooking.repository.WatchlistRepository;
import com.harshit.moviebooking.security.CustomUserDetails;
import com.harshit.moviebooking.service.WatchlistService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WatchlistServiceImpl implements WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final MovieRepo movieRepository;
    private final UserRepo userRepository;

    public WatchlistServiceImpl(WatchlistRepository watchlistRepository,
                                MovieRepo movieRepository,
                                UserRepo userRepository) {
        this.watchlistRepository = watchlistRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }

    @Override
    public WatchlistItemDto add(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));
        User user = currentUser();

        Optional<WatchlistEntry> existing = watchlistRepository.findByUserAndMovie(user, movie);
        if (existing.isPresent()) {
            return WatchlistMapper.toItemDto(existing.get());
        }

        WatchlistEntry entry = new WatchlistEntry();
        entry.setUser(user);
        entry.setMovie(movie);
        entry.setCreatedAt(LocalDateTime.now());
        return WatchlistMapper.toItemDto(watchlistRepository.save(entry));
    }

    @Override
    public void remove(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));
        User user = currentUser();
        watchlistRepository.findByUserAndMovie(user, movie)
                .ifPresent(watchlistRepository::delete);
    }

    @Override
    public List<WatchlistItemDto> listMine() {
        return watchlistRepository.findByUserOrderByCreatedAtDesc(currentUser()).stream()
                .map(WatchlistMapper::toItemDto)
                .toList();
    }

    @Override
    public boolean isOnWatchlist(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));
        return watchlistRepository.existsByUserAndMovie(currentUser(), movie);
    }

    private User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Please log in first.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails details) {
            return details.getUser();
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Please log in first."));
    }
}
