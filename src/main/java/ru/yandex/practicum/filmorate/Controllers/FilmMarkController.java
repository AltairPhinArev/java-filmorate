package ru.yandex.practicum.filmorate.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.LikeInputDto;
import ru.yandex.practicum.filmorate.service.FilmMarkService;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmMarkController {
    private final FilmMarkService filmService;

    @Autowired
    public FilmMarkController(FilmMarkService filmService) {
        this.filmService = filmService;
    }

    @GetMapping(value = "/mark/popular")
    public List<Film> getMostPopularFilmByAVG(
            @RequestParam(value = "count", defaultValue = "10") int limit,
            @RequestParam(value = "genreId", required = false) Integer genreId,
            @RequestParam(value = "year", required = false) Integer year) {
        return filmService.getRateFilmsByAVG(limit, genreId, year);
    }

    @PutMapping(value = "{id}")
    public void addMarkToFilm(@PathVariable Long id, @RequestBody LikeInputDto likeInputDto) {
        filmService.addMark(id, likeInputDto);
    }
}