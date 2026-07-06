package com.harshit.moviebooking.dto.movie;

import com.harshit.moviebooking.enums.ContentRating;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequestDto {
    private String title;
    private String synopsis;
    private LocalDate releaseDate;
    private Integer runtimeMinutes;
    private String language;
    private String countryOfOrigin;
    private ContentRating contentRating;
    private String posterUrl;
}
