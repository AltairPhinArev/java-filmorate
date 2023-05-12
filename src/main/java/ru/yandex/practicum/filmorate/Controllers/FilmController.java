package ru.yandex.practicum.filmorate.Controllers;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@RestController
public class FilmController {
    private int filmId = 1;
    private static final Logger log = LogManager.getLogger(Film.class);
    private final List<Film> films = new ArrayList<>();
    private final HashMap<Integer, Film> filmMap = new HashMap<>();

    @GetMapping(value = "/films")
    public Collection<Film> findAll() {
        log.info("Текущее количество фильмов {}", filmMap.size());
        return filmMap.values();
    }

    @PostMapping(value = "/films")
    public Film createFilm(@RequestBody Film film) {
        validate(film);
        film.setId(filmId++);
        filmMap.put(film.getId(), film);
        log.info("Фильм успшно добавлен");
        return film;
    }

    @PutMapping(value = "/films")
    public Film updateUser(@RequestBody Film film) {
        if (!filmMap.containsKey(film.getId())) {
            throw new ValidationException();
        } else {
            validate(film);
            filmMap.put(film.getId(), film);
            log.info("Фильм успешно обнавлен с id {}", film.getId());
            return film;
        }
    }

    private Film validate(Film film) {
        if (film.getName() != null &&
                !film.getName().isEmpty() &&
                film.getReleaseDate().isAfter(LocalDate.of(1895, 1, 28)) &&
                film.getDescription().length() < 200 && film.getDuration() > 0) {
            return film;
        } else {
            log.error("Не верно укзаны данные фильма");
            throw new ValidationException();
        }
    }
}