package ru.yandex.practicum.filmorate.serviceTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.Exceptions.UserOrFilmNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

public class FilmServiceTest {

    FilmService filmService;
    FilmStorage filmStorage;
    UserStorage userStorage;

    @BeforeEach
    public void setUp() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(userStorage, filmStorage);
    }

    @Test
    public void shouldAddLikeToFilm() {
        Film film = new Film("Star-Wars", "Galactic war",
                LocalDate.of(2000, 10, 10), 10);

        User user = new User("andrr@mail.ru", "null", "",
                LocalDate.of(1999, 10, 10));

        filmStorage.createFilm(film);
        userStorage.createUser(user);

        filmService.addLike(film.getId(), user.getId());
        Assertions.assertEquals(1, film.getLikes());
    }

    @Test
    public void shouldDeleteLikeToFilm() {
        Film film = new Film("Star-Wars", "Galactic war",
                LocalDate.of(2000, 10, 10), 10);

        User user = new User("andrr@mail.ru", "null", "",
                LocalDate.of(1999, 10, 10));

        filmStorage.createFilm(film);
        userStorage.createUser(user);

        filmService.addLike(film.getId(), user.getId());
        filmService.deleteLike(film.getId(), user.getId());
        Assertions.assertEquals(0, film.getLikes());
    }

    @Test
    public void shouldNotAddLikeToFilm() {
        Film film = new Film("Star-Wars", "Galactic war",
                LocalDate.of(2000, 10, 10), 10);

        User user = new User("andrr@mail.ru", "null", "",
                LocalDate.of(1999, 10, 10));

        filmStorage.createFilm(film);
        userStorage.createUser(user);

        Assertions.assertThrows(UserOrFilmNotFoundException.class, () -> {
            filmService.addLike(10L, 10L);
        });
    }

    @Test
    public void shouldNotDeleteLikeToFilm() {
        Film film = new Film("Star-Wars", "Galactic war",
                LocalDate.of(2000, 10, 10), 10);

        User user = new User("andrr@mail.ru", "null", "",
                LocalDate.of(1999, 10, 10));

        filmStorage.createFilm(film);
        userStorage.createUser(user);

        Assertions.assertThrows(UserOrFilmNotFoundException.class, () -> {
            filmService.deleteLike(10L, 10L);
        });
    }
}

