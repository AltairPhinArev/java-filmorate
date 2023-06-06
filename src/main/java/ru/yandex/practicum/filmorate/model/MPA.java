package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class MPA {

    private Long MPAid;
    private String ratingMPA;

    public MPA(Long MPAid, String ratingMPA) {
        this.MPAid = MPAid;
        this.ratingMPA = ratingMPA;
    }
}
