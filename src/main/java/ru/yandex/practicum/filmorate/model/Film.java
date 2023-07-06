package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Getter
@Setter
@EqualsAndHashCode
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
}
