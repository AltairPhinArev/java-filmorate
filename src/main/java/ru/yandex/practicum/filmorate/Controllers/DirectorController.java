package ru.yandex.practicum.filmorate.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Set;

@RequestMapping("/directors")
@RestController
public class DirectorController {

    private final DirectorService service;

    @Autowired
    public DirectorController(DirectorService service) {
        this.service = service;
    }

    @GetMapping
    public Set<Director> getDirectorSet() {
        return service.getDirectorSet();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable int id) {
        return service.getDirectorById(id).get();
    }

    @PostMapping
    public Director createDirector(@RequestBody Director director) {
        return service.createDirector(director);
    }

    @PutMapping
    public Director updateDirector(@RequestBody Director director) {
        return service.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void removeDirector(@PathVariable int id) {
        service.removeDirectorById(id);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpStatus handleNotFoundException(NotFoundException exception) {
        return HttpStatus.NOT_FOUND;
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HttpStatus handleValidationException(ValidationException exception) {
        return HttpStatus.BAD_REQUEST;
    }
}
