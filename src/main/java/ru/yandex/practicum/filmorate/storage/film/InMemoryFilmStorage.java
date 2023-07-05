package ru.yandex.practicum.filmorate.storage.film;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private Integer filmId = 1;
    private static final Logger log = LogManager.getLogger(Film.class);
    private final HashMap<Integer, Film> filmById = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        log.info("Current number of films {}", filmById.size());
        return filmById.values();
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(filmId++);
        filmById.put(film.getId(), film);
        log.info("Film has been crated successful" + film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!filmById.containsKey(film.getId())) {
            throw new ValidationException("Cannot find Film with this id");
        } else {
            filmById.put(film.getId(), film);
            log.info(film.getName() + " has been updated");
            return film;
        }
    }

    @Override
    public Film getFilmById(Integer id) {
        if (filmById.containsKey(id)) {
            return filmById.get(id);
        } else {
            throw new NotFoundException("Cannot find Film with this id");
        }
    }

    @Override
    public void deleteFilmById(Integer id) {
        if (filmById.containsKey(id)) {
            filmById.remove(id);
        } else {
            throw new ValidationException("Cannot find Film with this id");
        }
    }
}
