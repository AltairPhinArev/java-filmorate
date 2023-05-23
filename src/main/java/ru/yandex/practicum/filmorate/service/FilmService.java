package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exceptions.UserOrFilmNotFoundException;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;

import java.util.*;

@Service
public class FilmService {

    /*
    UserStorage userStorage = new InMemoryUserStorage();
    FilmStorage filmStorage = new InMemoryFilmStorage();
    */

    Set<Film> ratedFilms = new HashSet<>();
    HashMap<Long, Film> films = new HashMap<>();

    public void addLike(Film filmId , User userId) {
        if (filmId != null && userId != null) {
            filmId.setScore(filmId.getScore() + 1);
            films.put(userId.getId() , filmId);
            ratedFilms.add(filmId);
        } else {
            throw new ValidationException("Film or User Incorrect");
        }
    }

    public List<Film> rateFilm(int count) {
        List<Film> popularFilms = new ArrayList<>(ratedFilms);
        popularFilms.sort(Comparator.comparingInt(Film::getScore));

        return popularFilms.subList(0, Math.min(count, popularFilms.size()));
    }

    public void deleteLike(Film film , Long userId) {
        if(films.containsKey(userId) && films.containsValue(film)) {
            if(films.get(userId).getScore() > 0) {
                films.get(userId).setScore(films.get(userId).getScore() - 1);
            }
        } else {
            throw new UserOrFilmNotFoundException("Illegal arguments for remove like");
        }
    }
}
