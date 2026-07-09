package com.harshit.moviebooking.entity;

import com.harshit.moviebooking.enums.ContentRating;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String synopsis;

    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @Column(name = "runtime_minutes", nullable = false)
    private Integer runtimeMinutes;

    @Column(nullable = false, length = 50)
    private String language;

    @Column(name = "country_of_origin", nullable = false, length = 100)
    private String countryOfOrigin;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_rating", nullable = false, length = 20)
    private ContentRating contentRating;

    @Column(name = "poster_url", columnDefinition = "TEXT")
    private String posterUrl;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;

    @Column(name = "rating_count")
    private Integer ratingCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "movie")
    private List<Rating> ratings;
}

