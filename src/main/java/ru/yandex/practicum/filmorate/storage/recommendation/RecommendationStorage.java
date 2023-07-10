package ru.yandex.practicum.filmorate.storage.recommendation;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface RecommendationStorage {

    //возвращает ближайшего по предпочтениям пользователя
    long getNearestUserId(long toId);

    //возвращает список фильмов, рекомендованных одним пользователем другому
    List<Film> recommendFilms(long fromId, long toId);
}
