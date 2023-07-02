package ru.yandex.practicum.filmorate.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Set;

@RestController
public class DirectorController {

    private final DirectorService service;

    @Autowired
    public DirectorController(DirectorService service) {
        this.service = service;
    }

    @GetMapping("/directors")
    public Set<Director> getDirectorSet() {
        return service.getDirectorSet();
    }

    @GetMapping("/directors/{id}")
    public Director getDirectorById(@PathVariable int id) {
        return service.getDirectorById(id);
    }

    @PostMapping("/directors")
    public Director createDirector(@RequestBody Director director) {
        return service.createDirector(director);
    }

    @PutMapping("/directors")
    public Director updateDirector(@RequestBody Director director) {
        return service.updateDirector(director);
    }

    @DeleteMapping("/directors/{id}")
    public void removeDirector(@PathVariable int id) {
        service.removeDirectorById(id);
    }
}
