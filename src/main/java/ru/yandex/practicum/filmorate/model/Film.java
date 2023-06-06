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

    private int duration;

    private int likes = 0;

    private String genre;

    private String rating_MPA;

    private final Set<Long> voytedUsers = new HashSet<>();

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
 /*
    public (Long id, String name, String description, LocalDate releaseDate, int duration, int likes, String genre, String rating_MPA) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = likes;
        this.genre = genre;
        this.rating_MPA = rating_MPA;
    }

  */
}
