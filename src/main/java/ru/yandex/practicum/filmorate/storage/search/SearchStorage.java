package ru.yandex.practicum.filmorate.storage.search;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface SearchStorage {
    List<Film> searchFilmByName(String partialName);

    List<Film> searchFilmByDirector(String partialName);

    List<Film> searchFilmByNameAndDirector(String partialName);
}
