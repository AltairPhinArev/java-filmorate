package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.recommendation.RecommendationStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class AbstractRecommendationService {
    private final UserDbStorage userStorage;
    protected RecommendationStorage recommendationStorage;

    @Autowired
    public AbstractRecommendationService(UserDbStorage userStorage) {
        this.userStorage = userStorage;
    }

    //возвращает рекомендации фильмов от одного человека для заданного пользователя
    public List<Film> recommendBySingleUser(long userId) {
        //вначале проверяем корректность userId
        if (!userStorage.isUserPresent(userId)) { //пользователь не найден
            log.error("NOT FOUNDED USER");
            throw new NotFoundException("Пользователь с идентификатором " + userId + " не найден.");
        }
        //ищем ближайшего по предпочтениям пользователя
        long fromId = recommendationStorage.getNearestUserId(userId);
        if (fromId == 0) { //такого не оказалось
            return new ArrayList<>(); //возвращаем пустой список
        }
        //если все нормально - выводим рекомендации
        return recommendationStorage.recommendFilms(fromId, userId);
    }
}
