package ru.yandex.practicum.filmorate.Controllers;

import org.springframework.web.service.annotation.PutExchange;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class FilmController {

    private int FilmId = 1;
    private static final Logger log = LogManager.getLogger(Film.class);
    private final List<Film> films = new ArrayList<>();

    @GetMapping(value = "/films")
    public List<Film> findAll() {
        log.debug("Текущее количество фильмов {}", films.size());
        return films;
    }

    @PostMapping(value = "/films")
    public void createUser(@RequestBody Film film) {
        validate(film);
        film.setId(FilmId++);
        films.add(film);
    }

    private Film validate(Film film) {
        if (film.getName() != null &&
                film.getReleaseDate().isAfter(LocalDate.of(1895, 1, 28)) &&
                film.getDescription().length() < 200 &&
                !film.getDuration().isNegative()) {
            return new Film();
        } else {
            throw new ValidationException();
        }
    }
    @PutExchange(value = "/films")
    public void updateUser(@RequestBody Film film) {
        Film updatedFilm = films.get(film.getId());
        validate(updatedFilm);
        updatedFilm.setName(film.getName());
        updatedFilm.setDescription(film.getDescription());
        updatedFilm.setReleaseDate(film.getReleaseDate());
        updatedFilm.setDuration(film.getDuration());

        films.set(film.getId(), updatedFilm);
    }
}