package ru.yandex.practicum.filmorate.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.rateFilms.LikeDbStorage;


import java.time.LocalDate;
import java.util.*;

@Service
public class FilmService {

    FilmStorage filmStorage;
    LikeDbStorage likeDbStorage;
    JdbcTemplate jdbcTemplate;

    private static final Logger log = LogManager.getLogger(Film.class);

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, LikeDbStorage likeDbStorage,
                       JdbcTemplate jdbcTemplate) {
        this.filmStorage = filmStorage;
        this.likeDbStorage = likeDbStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film createFilm(Film film) {
        validate(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        validate(film);
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public void deleteFilmById(Long id) {
        filmStorage.deleteFilmById(id);
    }

    public List<Film> commonFilms(Long userId, Long friendId) {
        return likeDbStorage.findCommonFilms(userId, friendId);
    }

    public void addLike(Long filmId, Long userId) {
        likeDbStorage.addLike(filmId, userId);
    }

    public List<Film> getRateFilmsByCount(int count) {
        return likeDbStorage.getRateFilmsByCount(count);
    }

    public List<Long> getLikes(Long filmId) {
        return likeDbStorage.getLikes(filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        likeDbStorage.deleteLike(filmId, userId);
    }

    private Film validate(Film film) {
        if (film.getName() != null &&
                !film.getName().isEmpty() &&
                film.getReleaseDate().isAfter(LocalDate.of(1895, 1, 28)) &&
                film.getDescription().length() < 200 && film.getDuration() > 0) {
            return film;
        } else {
            log.error("Illegal arguments for Film");
            throw new ValidationException("Illegal arguments for Film");
        }
    }
}