package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@EqualsAndHashCode
public class Director {
    private int id;

    @NotBlank
    private String name;

    public Director() {
        super();
    }

    public Director(int id, String name) {
        super();
        this.id = id;
        this.name = name;
    }
}
