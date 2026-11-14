package com.example.ticket.movie;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

public record MovieRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 200) String director,
        @NotNull @Positive Integer durationMin,
        @NotNull Rating rating,
        @URL @Size(max = 500) String posterURL,
        String synopsis
) {
}