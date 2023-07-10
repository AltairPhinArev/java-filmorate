package ru.yandex.practicum.filmorate.Controllers;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@RestController
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping(value = "/films")
    public Collection<Film> findAllFilms() {
        return filmService.findAll();
    }

    @GetMapping(value = "/films/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping(value = "/films/popular")
    public List<Film> getMostPopularFilmByCount(
            @RequestParam(value = "count", defaultValue = "10") int limit,
            @RequestParam(value = "genreId", required = false) Integer genreId,
            @RequestParam(value = "year", required = false) Integer year) {
        return filmService.getRateFilmsByCount(limit, genreId, year);
    }


    @GetMapping(value = "/films/common")
    public List<Film> commonFilms(@RequestParam("userId") Long userId, @RequestParam("friendId") Long friendId) {
        return filmService.commonFilms(userId, friendId);
    }

    @PostMapping(value = "/films")
    public Film createFilm(@RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public void addLikeToFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping(value = "/films/{id}")
    public void deleteFilm(@PathVariable Long id) {
        filmService.deleteFilmById(id);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void deleteLikeFromFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/films/director/{directorId}")
    public Set<Film> getFilmsByDirectorId(@PathVariable("directorId") int directorId,
                                          @RequestParam(value = "sortBy", defaultValue = "likes") String sortedBy) {
        return filmService.getFilmsByDirectorId(directorId, sortedBy);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpStatus handleNegativeCount(final NotFoundException e) {
        return HttpStatus.NOT_FOUND;
    }
}