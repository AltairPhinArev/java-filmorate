package ru.yandex.practicum.filmorate.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exceptions.UserOrFilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.ratingMPA.MpaStorage;

import java.util.List;

@RestController
public class GenreController {

    GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping(value = "/genres")
    public List<Genre> getAll() {
        return genreService.getAll();
    }

    @GetMapping(value = "/genres/{id}")
    public Genre getGenreById(@PathVariable Byte id) {
        return genreService.getGenreById(id);
    }

    @ExceptionHandler(UserOrFilmNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpStatus handleNegativeCount(final UserOrFilmNotFoundException e) {
        return HttpStatus.NOT_FOUND;
    }
}
