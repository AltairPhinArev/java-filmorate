package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class Film {

    private Integer id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    private Integer duration;

    private Set<Genre> genres;

    private MPA mpa;

    private Set<Integer> voytedUsers;

    private Set<Director> directors;

    public Film(Integer id,
                String name,
                String description,
                LocalDate releaseDate,
                int duration,
                Set<Genre> genres,
                MPA mpa,
                Set<Integer> voytedUsers,
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

    public Film(Integer id,
                String name,
                String description,
                LocalDate releaseDate,
                Integer duration,
                Set<Genre> genres,
                MPA mpa,
                Set<Integer> voytedUsers) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;
        this.mpa = mpa;
        this.voytedUsers = voytedUsers;
    }

    public Film() {
    }
}
