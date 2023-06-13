package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Genre {

    private Byte id;
    private String name;

    public Genre(Byte id, String name) {
        this.id = id;
        this.name = name;
    }
}
