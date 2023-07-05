package ru.yandex.practicum.filmorate.storage.rateFilms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component
public class LikeDbStorage {

    JdbcTemplate jdbcTemplate;
    MpaService mpaService;
    GenreService genreService;

    private static final Logger log = LogManager.getLogger(Film.class);

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate, MpaService mpaService, GenreService genreService) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaService = mpaService;
        this.genreService = genreService;
    }

    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
        log.info("you just liked film");
    }

    public List<Film> getRateFilmsByCount(int limit, Integer genreId, Integer year) {
        String getPopularQuery = "SELECT id, name, description, release_date, duration, rating_id " +
                "FROM films LEFT JOIN film_likes ON films.id = film_likes.film_id " +
                "JOIN FILM_GENRES FG on FILMS.ID = FG.FILM_ID  ";
        List<Object> params = new ArrayList<>();
        if (genreId != null) {
            getPopularQuery += "WHERE GENRE_ID = ? ";
            params.add(genreId);
        }
        if (year != null) {
            if (params.isEmpty()) {
                getPopularQuery += "WHERE YEAR(FILMS.RELEASE_DATE) = ? ";
            } else {
                getPopularQuery += "AND YEAR(FILMS.RELEASE_DATE) = ? ";
            }
            params.add(year);
        }
        getPopularQuery += "GROUP BY films.id ORDER BY COUNT(film_likes.user_id) DESC LIMIT ?";

        log.info("Top films by count {}, genreId {}, year {}", limit, genreId, year);
        params.add(limit);

        return jdbcTemplate.query(getPopularQuery, params.toArray(), (rs, rowNum) -> new Film(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_Date").toLocalDate(),
                rs.getInt("duration"),
                new HashSet<>(getLikes(rs.getLong("id"))),
                new HashSet<>(genreService.getGenresByFilmId(rs.getLong("id"))),
                new MPA(rs.getInt("rating_id"),
                        mpaService.getMpaRateById(rs.getInt("rating_id")).getName())));
    }

    public List<Long> getLikes(Long filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                rs.getLong("user_id"), filmId);
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
}
