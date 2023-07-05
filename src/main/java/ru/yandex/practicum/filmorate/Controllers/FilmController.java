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
    public Film getFilmById(@PathVariable Integer id) {
        return filmService.getFilmById(id);
    }

    @GetMapping(value = "/films/popular")
    public List<Film> getMostPopularFilmByCount(@RequestParam (defaultValue = "10") Integer count) {
        return filmService.getRateFilmsByCount(count);
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
    public void addLikeToFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping(value = "/films/{id}")
    public void deleteFilm(@PathVariable Integer id) {
        filmService.deleteFilmById(id);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void deleteLikeFromFilm(@PathVariable Integer id, @PathVariable Integer userId) {
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