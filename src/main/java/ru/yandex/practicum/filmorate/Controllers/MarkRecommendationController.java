package ru.yandex.practicum.filmorate.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.RecommendationMarkService;

import java.util.List;

@RestController
public class MarkRecommendationController {
    private final RecommendationMarkService recommendationMarkService;

    @Autowired
    public MarkRecommendationController(RecommendationMarkService recommendationMarkService) {
        this.recommendationMarkService = recommendationMarkService;
    }

    @GetMapping(value = "/users/mark/{id}/recommendations")
    public List<Film> getRecommendation(@PathVariable("id") Long userId) {
        return recommendationMarkService.recommendBySingleUser(userId);
    }
}