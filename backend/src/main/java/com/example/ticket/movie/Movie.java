package com.example.ticket.movie;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

@Entity
@Table(name = "movie")
class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    @Size(max = 200)
    private String director;

    @Positive
    private int durationMin;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Rating rating;

    @NotNull
    private LocalDate releaseDate;

    @URL
    @Size(max = 500)
    private String posterUrl;

    private String synopsis;

    protected Movie(){

    }
    Movie(String title, String director, int durationMin,LocalDate releaseDate, Rating rating, String posterUrl, String synopsis) {
        this.title = title;
        this.director = director;
        this.durationMin = durationMin;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.posterUrl = posterUrl;
        this.synopsis = synopsis;
    }

    void update(String title, String director, int durationMin,LocalDate releaseDate, Rating rating, String posterUrl, String synopsis) {
        this.title = title;
        this.director = director;
        this.durationMin = durationMin;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.posterUrl = posterUrl;
        this.synopsis = synopsis;
    }

    Long getId() {return id;}
    String getTitle() {return title;}
    String getDirector() {return director;}
    int getDurationMin() {return durationMin;}
    LocalDate getReleaseDate() {return releaseDate;}
    Rating getRating() {return rating;}
    String getPosterUrl() {return posterUrl;}
    String getSynopsis() {return synopsis;}
}