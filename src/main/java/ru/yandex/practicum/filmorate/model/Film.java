package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private int size;

    private Long id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    private int duration;

    private int score = 0;

    private final Set<Long> voytedUsers = new HashSet<>();

    public int getSize() {
        return getVoytedUsers().size();
    }

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
    /*
    public Film(String name, String description, LocalDate releaseDate, int duration, ArrayList<Integer> isLiked) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.isLiked = isLiked;
    }
     */
}
