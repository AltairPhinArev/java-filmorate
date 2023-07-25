package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.feedTypes.Event;
import ru.yandex.practicum.filmorate.model.feedTypes.Operation;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.rateFilms.LikeDbStorage;

import java.util.List;

@Service
public class FilmService extends AbstractFilmService {

    @Autowired
    public FilmService(JdbcTemplate jdbcTemplate, DirectorService directorService, FeedService feedService,
                       @Qualifier("FilmDbStorage") FilmStorage filmStorage, LikeDbStorage likeStorage) {
        super(jdbcTemplate, directorService, feedService);
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
    }

    public void addLike(Long filmId, Long userId) {
        ((LikeDbStorage) likeStorage).addLike(filmId, userId);
        feedService.setOperation(userId, Event.LIKE, Operation.ADD, filmId);
    }

    public List<Film> getRateFilmsByCount(int limit, Integer genreId, Integer year) {
        return ((LikeDbStorage) likeStorage).getRateFilmsByCount(limit, genreId, year);
    }

    public List<Long> getLikes(Long filmId) {
        return ((LikeDbStorage) likeStorage).getLikes(filmId);
    }
}