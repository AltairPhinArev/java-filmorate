package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.LikeInputDto;
import ru.yandex.practicum.filmorate.model.feedTypes.Event;
import ru.yandex.practicum.filmorate.model.feedTypes.Operation;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.rateFilms.MarkDbStorage;

import java.util.List;
import java.util.Map;

@Service
public class FilmMarkService extends AbstractFilmService {

    @Autowired
    public FilmMarkService(JdbcTemplate jdbcTemplate, DirectorService directorService, FeedService feedService,
                           @Qualifier("FilmMarkDbStorage") FilmStorage filmStorage, MarkDbStorage likeStorage) {
        super(jdbcTemplate, directorService, feedService);
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
    }

    public void addMark(Long filmId, LikeInputDto likeInputDto) {
        ((MarkDbStorage) likeStorage).addMark(filmId, likeInputDto.getUserId(), likeInputDto.getMark());
        feedService.setOperation(likeInputDto.getUserId(), Event.LIKE, Operation.ADD, filmId);
    }


    public List<Film> getRateFilmsByAVG(int limit, Integer genreId, Integer year) {
        return ((MarkDbStorage) likeStorage).getRateFilmsByAVG(limit, genreId, year);
    }

    public Map<Long, Integer> getPoints(Long filmId) {
        return ((MarkDbStorage) likeStorage).getPoints(filmId);
    }
}