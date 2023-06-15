package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

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

    private Integer rate = 0;

    private Set<Genre> genres = new HashSet<>();

    private MPA mpa;

    private final Set<Long> voytedUsers = new HashSet<>();
    /*
    @Autowired
    public Film(String name, String description, LocalDate releaseDate, Integer duration, MPA mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

     */


    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration, MPA mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;

        this.mpa = mpa;
    }


}
