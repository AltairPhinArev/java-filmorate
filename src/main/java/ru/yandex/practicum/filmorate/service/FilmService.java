package ru.yandex.practicum.filmorate.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.Exceptions.UserOrFilmNotFoundException;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;

import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Service
public class FilmService {

    UserStorage userStorage;
    MpaService mpaService;
    FilmStorage filmStorage;
    JdbcTemplate jdbcTemplate;

    private static final Logger log = LogManager.getLogger(Film.class);

    @Autowired
    public FilmService(@Qualifier("UserDbStorage") UserStorage userStorage,
                       @Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       MpaService mpaService, JdbcTemplate jdbcTemplate) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.mpaService = mpaService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public void deleteFilmById(Long id) {
        filmStorage.deleteFilmById(id);
    }

    public void addLike(Long filmId,Long userId) {
       String sqlQuery = "INSERT IGNORE INTO films_likes (film_id, user_id)" +
               "VALUES (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, filmId);
            stmt.setLong(2, userId);
            return stmt;
        }, keyHolder);
        log.info("you just liked film");
    }

    public List<Film> getRateFilmsByCount(int count) {
        String sqlQuery = "SELECT * FROM films DESC LIMIT ?";
        try {
            return jdbcTemplate.query(sqlQuery, new Object[]{count}, (resultSet, rowNum) -> {
                Film film = new Film(null,null,null,
                          null,null, null);

                film.setId(resultSet.getLong("id"));
                film.setName(resultSet.getString("name"));
                film.setDescription(resultSet.getString("description"));
                film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
                film.setDuration(resultSet.getInt("duration"));
                film.setMpa(mpaService.getMpaRateById(resultSet.getInt("rating_id")));
                return film;
            });
        } catch (UserOrFilmNotFoundException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void deleteLike(Long id, Long userId) {
        User user = userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(id);

        if (film.getVoytedUsers().contains(user.getId())) {
            film.setRate(film.getRate() - 1);
            film.getVoytedUsers().remove(userId);
        } else {
            throw new UserOrFilmNotFoundException("User has not voted for the Film");
        }
    }
}