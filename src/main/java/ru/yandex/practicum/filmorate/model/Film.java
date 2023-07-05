package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
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

    @Builder
    public Film(long id,
                String name,
                String description,
                LocalDate releaseDate,
                int duration,
                Set<Genre> genres,
                MPA mpa,
                Set<Long> voytedUsers,
                Set<Director> directors) {
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
