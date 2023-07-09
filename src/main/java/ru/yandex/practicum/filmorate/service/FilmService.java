package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.feed.Event;
import ru.yandex.practicum.filmorate.storage.feed.Operation;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.rateFilms.LikeDbStorage;


import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class FilmService {

    FilmStorage filmStorage;
    LikeDbStorage likeDbStorage;
    JdbcTemplate jdbcTemplate;

    FeedService feedService;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, LikeDbStorage likeDbStorage,
                       JdbcTemplate jdbcTemplate, FeedService feedService) {
        this.filmStorage = filmStorage;
        this.likeDbStorage = likeDbStorage;
        this.jdbcTemplate = jdbcTemplate;
        this.feedService = feedService;
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
        feedService.setOperation(userId, Event.LIKE, Operation.ADD,filmId);
    }

    public List<Film> getRateFilmsByCount(int count) {
        return likeDbStorage.getRateFilmsByCount(count);
    }

    public List<Long> getLikes(Long filmId) {
        return likeDbStorage.getLikes(filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        likeDbStorage.deleteLike(filmId, userId);
        feedService.setOperation(userId, Event.LIKE, Operation.REMOVE, filmId);
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