package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Genre {

    private Long genreId;
    private String genre;

    public Genre(Long genreId, String genre) {
        this.genreId = genreId;
        this.genre = genre;
    }
}
