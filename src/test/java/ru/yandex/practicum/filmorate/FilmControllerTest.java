package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.Controllers.FilmController;

import java.time.LocalDate;

public class FilmControllerTest {

    FilmController filmController;

    @BeforeEach
    public void setUp() {
        filmController = new FilmController();
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
        Film film = new Film("Star-Wars" , "Galactic war" ,
                LocalDate.of(2000, 10, 10) , 0);
        Assertions.assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });
    }

    @Test
    public void shouldNotСreateFilmWithWrongDescription() {
        Film film = new Film("Star-Wars", "Galactic war" ,
                LocalDate.of(2000, 10, 10) , 0);
        Assertions.assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });
    }

    @Test
    public void shouldCreateFilm() {
        Film film = new Film("Star-Wars", "Galactic war" ,
                LocalDate.of(2000, 10, 10), 1);
        film.setId(1);
        Assertions.assertEquals(film , filmController.createFilm(film));
    }

    @Test
    public void shouldNotCreateFilmWithEmptyName() {
        Film film = new Film("", "Galactic war",
                LocalDate.of(2000, 10 , 10) , 12);
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