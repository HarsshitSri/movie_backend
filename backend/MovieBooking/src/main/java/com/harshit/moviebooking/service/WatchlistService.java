package com.harshit.moviebooking.service;

import com.harshit.moviebooking.dto.watchlist.WatchlistItemDto;

import java.util.List;

public interface WatchlistService {

    WatchlistItemDto add(Long movieId);

    void remove(Long movieId);

    List<WatchlistItemDto> listMine();

    boolean isOnWatchlist(Long movieId);
}
