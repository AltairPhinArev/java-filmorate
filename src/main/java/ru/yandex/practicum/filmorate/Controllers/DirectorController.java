package ru.yandex.practicum.filmorate.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
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
    public Director createDirector(@Valid @RequestBody Director director) {
        return service.createDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        return service.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void removeDirector(@PathVariable int id) {
        service.removeDirectorById(id);
    }
}
