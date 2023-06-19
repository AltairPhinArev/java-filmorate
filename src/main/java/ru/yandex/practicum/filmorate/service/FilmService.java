package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.rateFilms.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class FilmService {

    UserStorage userStorage;
    MpaService mpaService;
    FilmStorage filmStorage;
    LikeDbStorage likeDbStorage;
    JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmService(@Qualifier("UserDbStorage") UserStorage userStorage,
                       @Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       MpaService mpaService,LikeDbStorage likeDbStorage, JdbcTemplate jdbcTemplate) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.mpaService = mpaService;
        this.likeDbStorage = likeDbStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public void deleteFilmById(Long id) {
        filmStorage.deleteFilmById(id);
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
}