package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.feedTypes.Event;
import ru.yandex.practicum.filmorate.model.feedTypes.Operation;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.rateFilms.LikeDbStorage;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class FilmService {

    FilmStorage filmStorage;
    LikeDbStorage likeDbStorage;
    FeedService feedService;
    DirectorService directorService;

    JdbcTemplate jdbcTemplate;


    @Autowired

    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, LikeDbStorage likeDbStorage,
                       JdbcTemplate jdbcTemplate, DirectorService directorService, FeedService feedService) {

        this.filmStorage = filmStorage;
        this.likeDbStorage = likeDbStorage;
        this.jdbcTemplate = jdbcTemplate;
        this.directorService = directorService;
        this.feedService = feedService;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film createFilm(Film film) {
        validate(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        validate(film);
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public void deleteFilmById(Long id) {
        filmStorage.deleteFilmById(id);
    }

    public List<Film> commonFilms(Long userId, Long friendId) {
        return likeDbStorage.findCommonFilms(userId, friendId);
    }

    public void addLike(Long filmId, Long userId) {
        likeDbStorage.addLike(filmId, userId);
        feedService.setOperation(userId, Event.LIKE, Operation.ADD, filmId);
    }

    public List<Film> getRateFilmsByCount(int limit, Integer genreId, Integer year) {
        return likeDbStorage.getRateFilmsByCount(limit, genreId, year);
    }


    public List<Long> getLikes(Long filmId) {
        return likeDbStorage.getLikes(filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        likeDbStorage.deleteLike(filmId, userId);
        feedService.setOperation(userId, Event.LIKE, Operation.REMOVE, filmId);
    }


    /*
     Получить и отсортировать сет фильмов по ид режиссера
     */
    public Set<Film> getFilmsByDirectorId(int directorId, String sortBy) {
        if (!(sortBy.equals("likes")) && !(sortBy.equals("year"))) {
            throw new ValidationException("Можно сортировать только по годам или лайкам");
        }
        Director director = directorService.getDirectorById(directorId);

        TreeSet<Film> comparingByYear = new TreeSet<>(
            Comparator.comparing(Film::getReleaseDate)
        );

        TreeSet<Film> comparingByLikes = new TreeSet<>(
            (o1, o2) -> {
                if (o1.getVoytedUsers().size() != o2.getVoytedUsers().size()) {
                    return o1.getVoytedUsers().size() - o2.getVoytedUsers().size();
                } else {
                    return (int) (o1.getId() - o2.getId());
                }
            }
        );

        List<Integer> filmsId = getFilmsIds(directorId);

        Set<Film> films = new HashSet<>();

        for (Integer integer : filmsId) {
            films.add(getFilmById(((long) integer)));
        }

        switch (sortBy) {
            case "year":
                comparingByYear.addAll(films);
                return comparingByYear;
            case "likes":
                comparingByLikes.addAll(films);
                return comparingByLikes;
            default:
                throw new NotFoundException("Не получилось собрать фильмы по режиссеру");
        }
    }

    private List<Integer> getFilmsIds(int directorId) {
        String sqlQuery = "SELECT * FROM film_directors WHERE director_id=?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> getFilmsId(rs), directorId);
    }

    private int getFilmsId(ResultSet rs) throws SQLException {
        return rs.getInt("film_id");
    }

    private Film validate(Film film) {
        if (film.getName() != null &&
                !film.getName().isEmpty() &&
                film.getReleaseDate().isAfter(LocalDate.of(1895, 1, 28)) &&
                film.getDescription().length() < 200 && film.getDuration() > 0) {
            return film;
        } else {
            log.error("Illegal arguments for Film");
            throw new ValidationException("Illegal arguments for Film");
        }
    }
}