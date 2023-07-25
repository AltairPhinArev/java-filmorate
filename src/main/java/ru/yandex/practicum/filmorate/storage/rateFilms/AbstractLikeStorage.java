package ru.yandex.practicum.filmorate.storage.rateFilms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

public abstract class AbstractLikeStorage {
    JdbcTemplate jdbcTemplate;
    MpaService mpaService;
    GenreService genreService;
    DirectorService directorService;

    protected static final Logger log = LogManager.getLogger(Film.class);

    public AbstractLikeStorage(JdbcTemplate jdbcTemplate, MpaService mpaService, GenreService genreService,
                               DirectorService directorService) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaService = mpaService;
        this.genreService = genreService;
        this.directorService = directorService;
    }

    public List<Film> findCommonFilms(Long userId, Long friendId) {
        String sqlQuery = "SELECT f.* " +
                "FROM films f " +
                "JOIN film_likes fl1 ON fl1.film_id = f.id AND fl1.user_id = ? " +
                "JOIN film_likes fl2 ON fl2.film_id = f.id AND fl2.user_id = ? " +
                "JOIN (SELECT film_id, COUNT(user_id) AS rate FROM film_likes GROUP BY film_id) " +
                "AS fl ON fl.film_id = f.id " +
                "ORDER BY fl.rate DESC";
        return buildFilmFromQuery(sqlQuery, new Object[]{userId, friendId});
    }

    public void deleteLike(Long filmId, Long userId) {
        String checkQuery = "SELECT COUNT(*) FROM film_likes WHERE film_id = ? AND user_id = ?";

        try {
            int count = jdbcTemplate.queryForObject(checkQuery, Integer.class, filmId, userId);
            if (count > 0) {
                String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
                jdbcTemplate.update(sql, filmId, userId);
            } else {
                throw new NotFoundException(HttpStatus.NOT_FOUND.toString());
            }
        } catch (NullPointerException e) {
            e.getMessage();
            e.fillInStackTrace();
        }
    }

    protected abstract List<Film> buildFilmFromQuery(String sqlQuery, Object[] params);
}
