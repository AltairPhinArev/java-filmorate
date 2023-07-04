package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.recommendation.RecommendationStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecommendationService {
    private final UserDbStorage userStorage;
    private final RecommendationStorage recommendationStorage;

    @Autowired
    public RecommendationService(UserDbStorage userStorage, RecommendationStorage recommendationStorage) {
        this.userStorage = userStorage;
        this.recommendationStorage = recommendationStorage;
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

    //возвращает рекомендации фильмов от нескольких человек для заданного пользователя
    public List<Film> recommendBySeveralUsers(long userId) {
        //вначале проверяем корректность userId
        if (!userStorage.isUserPresent(userId)) { //пользователь не найден
            log.error("NOT FOUNDED USER");
            throw new NotFoundException("Пользователь с идентификатором " + userId + " не найден.");
        }
        //создаем множество фильмов
        Set<Film> films = new HashSet<>();
        //ищем пользователей, максимально пересекающихся с userId
        List<Long> fromIds = recommendationStorage.getNearestUserIds(userId);
        //выводим рекомендации
        for (Long fromId : fromIds) {
            films.addAll(recommendationStorage.recommendFilms(fromId, userId));
        }
        return films.stream().sorted(Comparator.comparingLong(Film::getId)).collect(Collectors.toList());
    }
}
