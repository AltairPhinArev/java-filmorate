package ru.yandex.practicum.filmorate.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.Exceptions.UserOrFilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;


@RestController
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;
    private final UserStorage userStorage;


    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
        this.userStorage = userStorage;
    }

    @GetMapping(value = "/films")
    public Collection<Film> findAllFilms() {
       return filmStorage.findAll();
    }

    @GetMapping(value = "/films/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return filmStorage.getFilmById(id);
    }

    @GetMapping(value = "/films/popular")
    public List<Film> getMostPopularFilm() {
        return filmService.getAllRatedFilms();
    }

    @GetMapping(value = "/films/popular?count={count}")
    public List<Film> getMostPopularFilmByCount(@PathVariable int count) {
        return filmService.getRateFilmsByCount(count);
    }

    @PostMapping(value = "/films")
    public Film createFilm(@RequestBody Film film) {
        return filmStorage.createFilm(film);
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public void addLikeToFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(filmStorage.getFilmById(id), userStorage.getUserById(userId));
    }

    @DeleteMapping(value = "/films/{id}")
    public void deleteFilm(@PathVariable Long id) {
        filmStorage.deleteFilmById(id);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void deleteLikeFromFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLike(filmStorage.getFilmById(id), userId);
    }

    @ExceptionHandler(UserOrFilmNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpStatus handleNegativeCount(final UserOrFilmNotFoundException e) {
        return HttpStatus.NOT_FOUND;
    }
}