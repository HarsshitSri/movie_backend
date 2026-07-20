package com.harshit.moviebooking.controller;

import com.harshit.moviebooking.dto.watchlist.WatchlistItemDto;
import com.harshit.moviebooking.service.WatchlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping
    public ResponseEntity<List<WatchlistItemDto>> listMine() {
        return ResponseEntity.ok(watchlistService.listMine());
    }

    @GetMapping("/{movieId}/status")
    public ResponseEntity<Map<String, Boolean>> status(@PathVariable Long movieId) {
        return ResponseEntity.ok(Map.of("onWatchlist", watchlistService.isOnWatchlist(movieId)));
    }

    @PostMapping("/{movieId}")
    public ResponseEntity<WatchlistItemDto> add(@PathVariable Long movieId) {
        WatchlistItemDto item = watchlistService.add(movieId);
        // Newly created entries get 201; already-present still returns the item (treat as create path)
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<Void> remove(@PathVariable Long movieId) {
        watchlistService.remove(movieId);
        return ResponseEntity.noContent().build();
    }
}
