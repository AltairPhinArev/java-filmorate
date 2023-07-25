package ru.yandex.practicum.filmorate.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.SearchMarkService;

import java.util.List;

@RestController
@RequestMapping(value = "/films/mark/search")
public class MarkSearchController {
    private final SearchMarkService searchMarkService;

    @Autowired
    public MarkSearchController(SearchMarkService searchMarkService) {
        this.searchMarkService = searchMarkService;
    }

    @GetMapping
    public List<Film> searchFilms(@RequestParam(defaultValue = "") String query,
                                  @RequestParam(defaultValue = "director,title") String by) {
        return searchMarkService.searchFilms(query, by);
    }
}