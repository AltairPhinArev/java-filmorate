package ru.yandex.practicum.filmorate.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.Exceptions.UserOrFilmNotFoundException;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    UserStorage userStorage;
    FilmStorage filmStorage;

    private static final Logger log = LogManager.getLogger(Film.class);

    public FilmService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
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

    public void addLike(Long filmId,Long userId) {
        if (filmStorage.getFilmById(filmId) != null && userStorage.getUserById(userId) != null) {
            if (!(filmStorage.getFilmById(filmId).getVoytedUsers().contains(userId))) {
                filmStorage.getFilmById(filmId).setLikes(filmStorage.getFilmById(filmId).getLikes() + 1);
                filmStorage.getFilmById(filmId).getVoytedUsers().add(userId);

                log.info("Like successfully has been added to Film with id {}",
                        filmStorage.getFilmById(filmId).getId());
            } else {
                log.error("you can't like Film twice");
                throw new ValidationException("Film or User Incorrect");
            }
        }
    }

    public List<Film> getRateFilmsByCount(int count) {
        List<Film> countedFilms = new ArrayList<>();

        if (filmStorage.getFilmsMap().keySet().size() >= count) {
            for (int i = 0; i < count; i++) {
                countedFilms.add(rateAndSortFilm().get(i));
            }
        } else {
            countedFilms.addAll(rateAndSortFilm());
        }
        return countedFilms;
    }

    public void deleteLike(Long id, Long userId) {
        User user = userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(id);

        if (film.getVoytedUsers().contains(user.getId())) {
            film.setLikes(film.getLikes() - 1);
            film.getVoytedUsers().remove(userId);
        } else {
            throw new UserOrFilmNotFoundException("User has not voted for the Film");
        }
    }

    private List<Film> rateAndSortFilm() {
        return filmStorage.getFilmsMap().keySet().stream()
                .map(id -> filmStorage.getFilmById(id)).sorted(Comparator.comparingInt(Film::getLikes)
                        .reversed()).collect(Collectors.toList());
    }
}
