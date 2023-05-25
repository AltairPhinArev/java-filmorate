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

@Service
public class FilmService {

    UserStorage userStorage;
    FilmStorage filmStorage;
    Set<Long> ratedFilmsIds = new HashSet<>();

    private static final Logger log = LogManager.getLogger(Film.class);

    public FilmService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public void addLike(Long filmId , Long userId) {
        for (Film filmIds : filmStorage.findAll()) {
            ratedFilmsIds.add(filmIds.getId());
        }
        if (filmStorage.getFilmById(filmId) != null && userStorage.getUserById(userId) != null) {
            if (!(filmStorage.getFilmById(filmId).getVoytedUsers().contains(userId))) {
                filmStorage.getFilmById(filmId).setScore(filmStorage.getFilmById(filmId).getScore() + 1);
                filmStorage.getFilmById(filmId).getVoytedUsers().add(userId);
                log.info("Like successfully has been added to film with id {}",
                        filmStorage.getFilmById(filmId).getId());
            } else {
                log.error("you can't like film twice");
                throw new ValidationException("Film or User Incorrect");
            }
        }
    }

    public List<Film> getRateFilmsByCount(int count) {
        List<Film> countedFilms = new ArrayList<>();
        for (Film filmId : filmStorage.findAll()) {
            ratedFilmsIds.add(filmId.getId());
        }
        if (rateAndSortFilm(ratedFilmsIds).size() >= count) {
            for (int i = 0; i < count; i++) {
                countedFilms.add(rateAndSortFilm(ratedFilmsIds).get(i));
            }
        } else if (rateAndSortFilm(ratedFilmsIds).size() < count) {
            countedFilms.addAll(rateAndSortFilm(ratedFilmsIds));
            return countedFilms;
        }
        return countedFilms;
    }

    private List<Film> rateAndSortFilm(Set<Long> ratedFilmsIds) {
        List<Film> ratedFilms = new ArrayList<>();
        for (Long id : ratedFilmsIds) {
            ratedFilms.add(filmStorage.getFilmById(id));
        }
        ratedFilms.sort(Comparator.comparingInt(Film::getScore)
                .reversed());
        return ratedFilms;
    }

    public void deleteLike(Long id, Long userId) {
        User user = userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(id);
        if (user != null && film != null) {
            if (film.getVoytedUsers().contains(user.getId())) {
                film.setScore(film.getScore() - 1);
                film.getVoytedUsers().remove(userId);
            } else {
                throw new UserOrFilmNotFoundException("User has not voted for the film");
            }
        } else {
            throw new ValidationException("Film or User is incorrect");
        }
    }
}
