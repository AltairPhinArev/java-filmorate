package ru.yandex.practicum.filmorate.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exceptions.UserOrFilmNotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
public class MPAController {

    MpaService mpaService;

    @Autowired
    public MPAController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping(value = "/mpa")
    public List<MPA> getAll() {
      return mpaService.getAllMpa();
    }

    @GetMapping(value = "/mpa/{id}")
    public MPA getMPAById(@PathVariable Integer id) {
        return mpaService.getMpaRateById(id);
    }

    @ExceptionHandler(UserOrFilmNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpStatus handleNegativeCount(final UserOrFilmNotFoundException e) {
        return HttpStatus.NOT_FOUND;
    }
}
