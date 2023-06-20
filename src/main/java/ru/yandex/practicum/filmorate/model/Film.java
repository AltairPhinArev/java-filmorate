package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class Film {

    private Long id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    private Integer duration;

    private Integer rate = 0;

    private Set<Genre> genres;

    private MPA mpa;

    private Set<Long> voytedUsers;

    @Builder
    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration,
                Set<Long> voytedUsers, Set<Genre> genres, MPA mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.voytedUsers = voytedUsers;
        this.genres = genres;
        this.mpa = mpa;
    }
}
