package ru.yandex.practicum.filmorate.Controllers;

import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class FilmController {

    private int filmId = 1;
    private static final Logger log = LogManager.getLogger(Film.class);
    private final List<Film> films = new ArrayList<>();

    @GetMapping(value = "/films")
    public List<Film> findAll() {
        log.debug("Текущее количество фильмов {}", films.size());
        return films;
    }

    @PostMapping(value = "/films")
    public Film createFilm(@RequestBody Film film) {
        validate(film);
        film.setId(filmId++);
        films.add(film);
        return film;
    }

    private Film validate(Film film) {
        if (film.getName() != null &&
                !film.getName().isEmpty() &&
                film.getReleaseDate().isAfter(LocalDate.of(1895, 1, 28)) &&
                film.getDescription().length() < 200 &&
                film.getDuration() > -1) {
            return film;
        } else {
            throw new ValidationException("Данные не верно указаны");
        }
    }

    @PutMapping(value = "/films")
    public Film updateUser(@RequestBody Film film) {
        if (filmId < film.getId()) {
            throw new ValidationException("Такого фильма пока нет(");
        } else {
            
            validate(film);
            films.set(film.getId() - 1, film);
            return film;
        }
    }
}