package ru.yandex.practicum.filmorate.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.rateFilms.LikeDbStorage;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Service
public class FilmService {

    FilmStorage filmStorage;
    LikeDbStorage likeDbStorage;

    DirectorDbStorage directorStorage;

    JdbcTemplate jdbcTemplate;

    private static final Logger log = LogManager.getLogger(Film.class);

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, LikeDbStorage likeDbStorage,
                       JdbcTemplate jdbcTemplate, DirectorDbStorage directorStorage) {
        this.filmStorage = filmStorage;
        this.likeDbStorage = likeDbStorage;
        this.jdbcTemplate = jdbcTemplate;
        this.directorStorage = directorStorage;
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

    public void addLike(Long filmId, Long userId) {
        likeDbStorage.addLike(filmId, userId);
    }

    public List<Film> getRateFilmsByCount(int count) {
        return likeDbStorage.getRateFilmsByCount(count);
    }

    public List<Long> getLikes(Long filmId) {
        return likeDbStorage.getLikes(filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        likeDbStorage.deleteLike(filmId, userId);
    }


    /*
     Получить и отсортировать сет фильмов по ид режиссера
     */
    public Set<Film> getFilmsByDirectorId(int directorId, String sortBy) {
        if (!(sortBy.equals("likes")) && !(sortBy.equals("year"))) {
            throw new ValidationException("Можно сортировать только по годам или лайкам");
        }
        directorStorage.getDirectorById(directorId)
                .orElseThrow(() -> new NotFoundException(String.format("Не нашли режиссера с ID: %d", directorId)));
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