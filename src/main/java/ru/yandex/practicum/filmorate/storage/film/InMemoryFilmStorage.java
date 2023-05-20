package ru.yandex.practicum.filmorate.storage.film;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private int filmId = 1;
    private static final Logger log = LogManager.getLogger(Film.class);
    private final HashMap<Integer, Film> filmById = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        log.info("Current number of films {}", filmById.size());
        return filmById.values();
    }

    @Override
    public Film createFilm(Film film) {
        validate(film);
        film.setId(filmId++);
        filmById.put(film.getId(), film);
        log.info("Film has been crated successful");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!filmById.containsKey(film.getId())) {
            throw new ValidationException("Cannot find film with this id");
        } else {
            validate(film);
            filmById.put(film.getId(), film);
            log.info("Film has been updated with id {}", film.getId());
            return film;
        }
    }

    @Override
    public Film getFilmById(int id) {
        if (filmById.containsKey(id)) {
            return filmById.get(id);
        } else {
            throw new ValidationException("Cannot find film with this id");
        }
    }

    @Override
    public void deleteFilmById(int id) {
        if (filmById.containsKey(id)) {
            filmById.remove(id);
        } else {
            throw new ValidationException("Cannot find film with this id");
        }
    }

    private Film validate(Film film) {
        if (film.getName() != null &&
                !film.getName().isEmpty() &&
                film.getReleaseDate().isAfter(LocalDate.of(1895, 1, 28)) &&
                film.getDescription().length() < 200 && film.getDuration() > 0) {
            return film;
        } else {
            log.error("Illegal arguments for film");
            throw new ValidationException("Illegal arguments for film");
        }
    }
}
