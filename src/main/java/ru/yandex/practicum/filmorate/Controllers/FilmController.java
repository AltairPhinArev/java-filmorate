package ru.yandex.practicum.filmorate.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;


@RestController
public class FilmController {

    private FilmStorage filmStorage;
    private FilmService filmService;
    private UserStorage userStorage;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService , UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
        this.userStorage = userStorage;
    }

    @GetMapping(value = "/films")
    public Collection<Film> findAllFilms() {
       return filmStorage.findAll();
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
    public void addLikeToFilm(@PathVariable int id , @PathVariable int userId) {
        filmService.addLike(filmStorage.getFilmById(id) , userStorage.getUserById(userId));
    }


    @GetMapping(value = "/films/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmStorage.getFilmById(id);
    }

    @DeleteMapping(value = "/films/{id}")
    public void deleteFilm(@PathVariable int id) {
        filmStorage.deleteFilmById(id);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void deleteLikeFromFilm(@PathVariable int id , @PathVariable int userId) {
        filmService.deleteLike(filmStorage.getFilmById(id) , userId);
    }
}