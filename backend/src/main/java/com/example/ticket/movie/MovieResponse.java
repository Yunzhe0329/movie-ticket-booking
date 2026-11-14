package com.example.ticket.movie;

import java.time.LocalDate;

public record MovieResponse(
        Long id,
        String title,
        String director,
        int durationMin,
        Rating rating,
        LocalDate releaseDate,
        String posterUrl,
        String synopsis
) {
    static MovieResponse from(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getDurationMin(),
                movie.getRating(),
                movie.getReleaseDate(),
                movie.getPosterUrl(),
                movie.getSynopsis()
        );
    }
}