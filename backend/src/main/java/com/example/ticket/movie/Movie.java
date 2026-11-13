package com.example.ticket.movie;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    private String director;
    private int durationMin;

    @Enumerated(EnumType.STRING)
    private Rating rating;

    private LocalDate releaseDate;
    private String posterUrl;
    private String synopsis;

    protected Movie(){

    }
    Movie(String title, String director, int durationMin, Rating rating, String posterUrl, String synopsis) {
        this.title = title;
        this.director = director;
        this.durationMin = durationMin;
        this.rating = rating;
        this.posterUrl = posterUrl;
        this.synopsis = synopsis;
    }

    void update(String title, String director, int durationMin, Rating rating, String posterUrl) {
        this.title = title;
        this.director = director;
        this.durationMin = durationMin;
        this.rating = rating;
        this.posterUrl = posterUrl;
        this.synopsis = synopsis;
    }

    Long getId() {return id;}
    String getTitle() {return title;}
    String getDirector() {return director;}
    int getDurationMin() {return durationMin;}

    Rating getRating() {return rating;}
    String getPosterUrl() {return posterUrl;}
    String getSynopsis() {return synopsis;}
}