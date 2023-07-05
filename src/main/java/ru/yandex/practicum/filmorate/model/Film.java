package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class Film {

    private Long id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    private Integer duration;

    private Set<Genre> genres;

    private MPA mpa;

    private Set<Long> voytedUsers;

    private Set<Director> directors;

    public Film(long id,
                String name,
                String description,
                LocalDate releaseDate,
                int duration,
                HashSet<Genre> genres,
                MPA mpa,
                HashSet<Long> voytedUsers,
                HashSet<Director> directors) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;
        this.mpa = mpa;
        this.voytedUsers = voytedUsers;
        this.directors = directors;
    }
}
