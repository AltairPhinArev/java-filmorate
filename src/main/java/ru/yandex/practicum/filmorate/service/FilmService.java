package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;


@Service
public class FilmService {

    Set<Film> ratedFilms = new HashSet<>();
    HashMap<Integer, Film> films = new HashMap<>();

    public void addLike(Film film , User user) {
        if (user != null && film != null) {
            film.setScore(film.getScore() + 1);
            films.put(user.getId(), film);
        } else {
            throw new ValidationException("Film or User Incorrect");
        }
    }

    public Set<Film> rateFilm(Film film) {
        return ratedFilms;
    }

    public void deleteLike(Film film , int userId) {
        if(films.containsKey(userId)) {
            films.remove(userId);
        } else {
            throw new ValidationException("Illegal arguments for remove like");
        }
    }
}
