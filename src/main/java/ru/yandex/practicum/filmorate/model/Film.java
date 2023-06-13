package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;

import java.util.Set;

@Data
public class Film {

    private Long id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    private Integer duration;

    private Integer likes = 0;

    private Set<Genre> genre = new HashSet<>();

    private MPA mpa;

    private final Set<Long> voytedUsers = new HashSet<>();

    public Film(String name, String description, LocalDate releaseDate, Integer duration, MPA mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

}
