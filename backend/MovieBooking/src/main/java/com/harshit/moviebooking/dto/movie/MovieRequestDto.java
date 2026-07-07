package com.harshit.moviebooking.dto.movie;

import com.harshit.moviebooking.enums.ContentRating;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Title is required.")
    @Size(max = 255, message = "Title cannot exceed 255 characters.")
    private String title;

    @NotBlank(message = "Synopsis is required.")
    @Size(max = 5000, message = "Synopsis cannot exceed 5000 characters.")
    private String synopsis;

    @NotNull(message = "Release date is required.")
    private LocalDate releaseDate;

    @NotNull(message = "Runtime is required.")
    @Min(value = 1, message = "Runtime must be at least 1 minute.")
    private Integer runtimeMinutes;

    @NotBlank(message = "Language is required.")
    @Size(max = 50, message = "Language cannot exceed 50 characters.")
    private String language;

    @NotBlank(message = "Country of origin is required.")
    @Size(max = 100, message = "Country cannot exceed 100 characters.")
    private String countryOfOrigin;

    @NotNull(message = "Content rating is required.")
    private ContentRating contentRating;

    @Size(max = 500, message = "Poster URL cannot exceed 500 characters.")
    private String posterUrl;
}