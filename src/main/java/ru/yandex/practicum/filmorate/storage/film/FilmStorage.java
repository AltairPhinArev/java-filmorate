package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;

public interface FilmStorage {

    Collection<Film> findAll();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(Long id);

    //HashMap<Long, Film> getFilmsMap();

    void deleteFilmById(Long id);

}
