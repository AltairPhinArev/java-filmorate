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

import java.util.Comparator;

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

    public void addLike(Film film , User user) {
        for (Film filmId : filmStorage.findAll()) {
            ratedFilmsIds.add(filmId.getId());
        }
        if (film != null) {
            if (!(film.getVoytedUsers().contains(user.getId()))) {
                film.setScore(film.getScore() + 1);
                film.getVoytedUsers().add(user.getId());
                log.info("Like successfully has been added to film with id {}", film.getId());
            } else {
                log.error("you can't like film twice");
                throw new ValidationException("Film or User Incorrect");
            }
        }
    }

    public List<Film> getRateFilmsByCount(int count) {
        List<Film> countedFilms = new ArrayList<>();
        if (count >= rateAndSortFilm(ratedFilmsIds).size()) {
           for (int i = 1; i < count - 1; i++) {
               countedFilms.add(rateAndSortFilm(ratedFilmsIds).get(i));
           }
           log.info("Size of countedFilms{}", countedFilms.size());
            return countedFilms;
        } else {
            throw new ValidationException("Illegal Arguments for Count");
        }
    }

    private List<Film> rateAndSortFilm(Set<Long> ratedFilmsIds) {
        List<Film> ratedFilms = new ArrayList<>();
        for (Long id : ratedFilmsIds) {
            ratedFilms.add(filmStorage.getFilmById(id));
        }
        ratedFilms.sort(Comparator.comparingInt(Film::getScore)
                .thenComparingLong(Film::getSize).reversed());
        return ratedFilms;
    }

    public List<Film> getAllRatedFilms() {
        if (rateAndSortFilm(ratedFilmsIds).size() < 10) {
            return rateAndSortFilm(ratedFilmsIds);
        } else {
            return rateAndSortFilm(ratedFilmsIds).subList(0, 10);
        }
    }

    public void deleteLike(Film film, Long userId) {
        User user = userStorage.getUserById(userId);
        if (user != null && film != null) {
            if (film.getVoytedUsers().contains(user.getId())) {
                film.setScore(film.getScore() - 1);
                // film.getVoytedUsers().remove(user);
                //ratedFilmsIds.remove(film.getId());
            } else {
                throw new UserOrFilmNotFoundException("User has not voted for the film");
            }
        } else {
            throw new ValidationException("Film or User is incorrect");
        }
    }
}
