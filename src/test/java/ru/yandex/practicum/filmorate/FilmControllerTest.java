package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.Controllers.FilmController;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

public class FilmControllerTest {

    FilmController filmController;
    FilmService filmService;
    FilmStorage filmStorage;
    UserStorage userStorage;

    @BeforeEach
    public void setUp() {
        filmService = new FilmService();
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmController = new FilmController(filmStorage , filmService , userStorage);
    }

    @Test
    public void shouldNotСreateFilmWithWrongRealeaseDate() {
        Film film = new Film("Star-Wars", "Galactic war",
                LocalDate.of(1700, 10, 10), 10);
        Assertions.assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });
    }

    @Test
    public void shouldNotСreateFilmWithWrongDuration() {
        Film film = new Film("Star-Wars", "Galactic war",
                LocalDate.of(2000, 10, 10), 0);
        Assertions.assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });
    }

    @Test
    public void shouldNotСreateFilmWithWrongDescription() {
        Film film = new Film("Star-Wars", "Galactic war",
                LocalDate.of(2000, 10, 10), 0);
        Assertions.assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });
    }

    @Test
    public void shouldCreateFilm() {
        Film film = new Film("Star-Wars", "Galactic war",
                LocalDate.of(2000, 10, 10), 1);
        film.setId(1);
        Assertions.assertEquals(film, filmController.createFilm(film));
    }

    @Test
    public void shouldNotCreateFilmWithEmptyName() {
        Film film = new Film("", "Galactic war",
                LocalDate.of(2000, 10, 10), 12);
        Assertions.assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });
    }

    @Test
    public void shouldNotCreateFilmWithNullName() {
        Film film = new Film(null, "Galactic war",
                LocalDate.of(2000, 10, 10), 12);
        Assertions.assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });
    }


}